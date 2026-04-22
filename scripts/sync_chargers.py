import json
import os
import xml.etree.ElementTree as ET
from typing import Iterable
from urllib.parse import unquote
from urllib.parse import urlparse

import requests


BASE_URL = "http://apis.data.go.kr/B552584/EvCharger"
SYNC_URL = os.getenv("CHARGING_SYNC_URL", "http://localhost:8080/api/sync/charging-stations")
SYNC_TOKEN = os.getenv("CHARGING_SYNC_TOKEN", "")
SYNC_EXECUTE = os.getenv("CHARGING_SYNC_EXECUTE", "").strip().lower() == "true"
SERVICE_KEY = os.getenv("CHARGING_API_KEY")
ROWS_PER_PAGE = int(os.getenv("CHARGING_ROWS", "10"))
MAX_PAGES = int(os.getenv("CHARGING_MAX_PAGES", "1"))
ZONE_CODES = ["11", "26", "27", "28", "29", "30", "31", "36", "41", "42", "43", "44", "45", "46", "47", "48", "50"]


def require_service_key() -> str:
    if SERVICE_KEY:
        return SERVICE_KEY
    raise ValueError("CHARGING_API_KEY environment variable is required.")


def ensure_sync_target_allowed() -> None:
    parsed = urlparse(SYNC_URL)
    hostname = (parsed.hostname or "").lower()
    if hostname in {"localhost", "127.0.0.1"}:
        return

    allow_remote_sync = os.getenv("CHARGING_ALLOW_REMOTE_SYNC", "").strip().lower() == "true"
    if not allow_remote_sync:
        raise ValueError(
            "Remote sync is blocked by default. "
            "Set CHARGING_ALLOW_REMOTE_SYNC=true only when you intentionally need it."
        )


def parse_xml_items(xml_text: str) -> list[dict]:
    root = ET.fromstring(xml_text)
    items = []
    for item in root.findall(".//item"):
        row = {}
        for child in item:
            row[child.tag] = child.text
        items.append(row)
    return items


def call_api(endpoint: str, params: dict) -> list[dict]:
    request_params = {
        "ServiceKey": unquote(require_service_key()),
        **params,
    }
    response = requests.get(f"{BASE_URL}/{endpoint}", params=request_params, timeout=60)
    response.raise_for_status()
    return parse_xml_items(response.text)


def get_charger_info(page_no: int, zcode: str) -> list[dict]:
    return call_api(
        "getChargerInfo",
        {
            "pageNo": page_no,
            "numOfRows": ROWS_PER_PAGE,
            "zcode": zcode,
        },
    )


def fetch_zone_items(zcode: str) -> list[dict]:
    zone_items: list[dict] = []

    for page_no in range(1, MAX_PAGES + 1):
        page_items = get_charger_info(page_no=page_no, zcode=zcode)
        if not page_items:
            break

        zone_items.extend(page_items)
        print(f"[{zcode}] page={page_no} fetched={len(page_items)} total={len(zone_items)}")

        if len(page_items) < ROWS_PER_PAGE:
            break

    return zone_items


def to_sync_row(item: dict) -> dict:
    return {
        "statId": item.get("statId", ""),
        "statNm": item.get("statNm", ""),
        "addr": item.get("addr", ""),
        "addrDetail": "",
        "locationDesc": item.get("location", ""),
        "lat": float(item.get("lat", 0.0)) if item.get("lat") else 0.0,
        "lng": float(item.get("lng", 0.0)) if item.get("lng") else 0.0,
        "useTime": item.get("useTime", ""),
    }


def unique_rows(items: Iterable[dict]) -> list[dict]:
    rows_by_stat_id: dict[str, dict] = {}
    for item in items:
        row = to_sync_row(item)
        stat_id = row["statId"]
        if stat_id:
            rows_by_stat_id[stat_id] = row
    return list(rows_by_stat_id.values())


def send_to_spring_boot(rows: list[dict]) -> None:
    if not SYNC_EXECUTE:
        print("Dry run only. Set CHARGING_SYNC_EXECUTE=true to post rows.")
        return

    ensure_sync_target_allowed()

    headers = {"Content-Type": "application/json"}
    if SYNC_TOKEN:
        headers["X-Sync-Token"] = SYNC_TOKEN

    response = requests.post(SYNC_URL, data=json.dumps(rows), headers=headers, timeout=120)
    print(f"sync_url={SYNC_URL}")
    print(f"status={response.status_code}")
    print(response.text)
    response.raise_for_status()


if __name__ == "__main__":
    all_items: list[dict] = []

    for zone_code in ZONE_CODES:
        zone_items = fetch_zone_items(zone_code)
        all_items.extend(zone_items)

    rows = unique_rows(all_items)
    print(f"total_raw_items={len(all_items)}")
    print(f"total_unique_rows={len(rows)}")

    if rows:
        send_to_spring_boot(rows)
    else:
        print("No charging rows fetched.")
