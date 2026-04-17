# 라이브러리를 먼저 설치해 주세요
# pip install requests

import os
import requests
import xml.etree.ElementTree as ET
from urllib.parse import unquote
import json # JSON 변환을 위해 추가


# ==============================
# 1. 공공데이터포털 서비스키 입력 (환경변수 사용)
# ==============================
# os.getenv()를 사용하여 환경변수에서 API 키를 안전하게 불러옵니다.
SERVICE_KEY = os.getenv("CHARGING_API_KEY")

# 환경변수가 설정되지 않은 경우 에러 발생
if not SERVICE_KEY:
    raise ValueError("환경변수 'CHARGING_API_KEY'가 설정되지 않았습니다. 실행 환경에서 API 키를 설정해주세요.")

# 공식 문서 기준 서비스 URL
BASE_URL = "http://apis.data.go.kr/B552584/EvCharger"


# ==============================
# 2. XML 응답 공통 파싱 함수
# ==============================
def parse_xml_items(xml_text: str) -> list[dict]:
    root = ET.fromstring(xml_text)
    items = []
    for item in root.findall(".//item"):
        row = {}
        for child in item:
            row[child.tag] = child.text
        items.append(row)
    return items


# ==============================
# 3. 공통 API 호출 함수
# ==============================
def call_api(endpoint: str, params: dict) -> list[dict]:
    url = f"{BASE_URL}/{endpoint}"
    request_params = {
        "ServiceKey": unquote(SERVICE_KEY),
        **params
    }
    response = requests.get(url, params=request_params, timeout=60)
    response.raise_for_status()
    return parse_xml_items(response.text)


# ==============================
# 4. 충전소 정보 조회
# ==============================
def get_charger_info(page_no: int = 1, num_of_rows: int = 10, zcode: str | None = None) -> list[dict]:
    params = {
        "pageNo": page_no,
        "numOfRows": num_of_rows,
    }
    if zcode:
        params["zcode"] = zcode
    return call_api("getChargerInfo", params)


# ==============================
# 5. 스프링 부트로 데이터 전송 (새로 추가된 부분)
# ==============================
def send_to_spring_boot(items: list[dict]):
    """
    공공데이터에서 가져온 items 리스트를 
    스프링 부트 DTO 형태에 맞게 가공하여 전송
    """
    url = "http://localhost:8080/api/sync/charging-stations"
    headers = {'Content-Type': 'application/json'}

    mapped_data = []
    for item in items:
        # 스프링 부트 DTO 필드명에 맞게 데이터 매핑
        row = {
            "statId": item.get("statId", ""),
            "statNm": item.get("statNm", ""),
            "addr": item.get("addr", ""),
            "addrDetail": "", # 공공데이터에 상세주소가 명확히 분리되어 있지 않으면 비워둠
            "locationDesc": item.get("location", ""), # 공공데이터의 location을 위치설명으로 사용
            # 위도 경도는 문자열로 오기 때문에 float 변환
            "lat": float(item.get("lat", 0.0)) if item.get("lat") else 0.0,
            "lng": float(item.get("lng", 0.0)) if item.get("lng") else 0.0,
            "useTime": item.get("useTime", "")
        }
        mapped_data.append(row)

    print(f"스프링 부트로 {len(mapped_data)}건의 데이터를 전송합니다...")
    
    try:
        # JSON 형태로 변환하여 POST 전송
        response = requests.post(url, data=json.dumps(mapped_data), headers=headers)
        print(f"스프링 부트 응답코드: {response.status_code}")
        print(f"스프링 부트 응답메시지: {response.text}")
    except Exception as e:
        print(f"전송 중 오류가 발생했습니다: {e}")


# ==============================
# 6. 테스트 실행
# ==============================
'''	# 특정 지역만 조회
	if __name__ == "__main__":
    print("=== 1. 공공데이터 충전소 정보 조회 ===")
  
    # 테스트를 위해 우선 10건만 조회
    info_items = get_charger_info(page_no=1, num_of_rows=10, zcode="11") 
    
    print(f"총 {len(info_items)}건의 데이터를 공공데이터 포털에서 가져왔습니다.")
    
    print("\n=== 2. 스프링 부트로 데이터 전송 ===")
    if info_items:
        send_to_spring_boot(info_items)
    else:
        print("가져온 데이터가 없어 전송하지 않습니다.")'''
        
    # 많은 지역 조회
if __name__ == "__main__":
    # 1. 지역 코드 리스트를 먼저 만듭니다.
    zone_codes = ["11", "26", "27", "28", "29", "30", "31", "36", "41", "42", "43", "44", "45", "46", "47", "48", "50"]
    
    # 2. for문을 사용해서 지역별로 하나씩 호출합니다.
    for code in zone_codes:
        print(f"\n=== [{code}] 지역 데이터 수집 및 전송 시작 ===")
        
        # num_of_rows= 각 지역마다 10개씩 데이타를 가져옵니다.
        # 여기서 zcode=code 라고 넣어주는 것이 핵심입니다!
        info_items = get_charger_info(page_no=1, num_of_rows=10, zcode=code)
        
        if info_items:
            send_to_spring_boot(info_items)
        else:
            print(f"{code} 지역에 데이터가 없습니다.")