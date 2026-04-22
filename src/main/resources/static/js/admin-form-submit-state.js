document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-submit-state-form]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            if (form.dataset.submitting === "true") {
                event.preventDefault();
                return;
            }

            const submitButton =
                event.submitter instanceof HTMLButtonElement
                    ? event.submitter
                    : form.querySelector('button[type="submit"]');

            if (!submitButton) {
                return;
            }

            form.dataset.submitting = "true";
            submitButton.dataset.originalText = submitButton.textContent.trim();
            submitButton.textContent = submitButton.dataset.loadingText || "저장 중...";
            submitButton.disabled = true;
        });
    });
});
