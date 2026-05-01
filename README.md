# Coaching Center Website Template (Multi-language + WhatsApp + Google Sheets)

A reusable landing page template for coaching centers built with **HTML, CSS, and Vanilla JavaScript**.
All editable content is in `config.json`.

## Files
- `index.html` - semantic page skeleton (no hardcoded content text)
- `style.css` - responsive modern UI styling
- `script.js` - dynamic rendering, language toggle, form handling, webhook posting
- `config.json` - all editable content and integration settings
- `README.md` - setup + deployment guide

## 1) Configure your center details
Open `config.json` and update:
- `center_name`
- `phone_number`
- `whatsapp_number` (country code + number, no `+`)
- `google_script_url`
- `subjects`, `timings`, `fees`
- `language_content` for English (`en`) and Urdu (`ur`)

## 2) Google Sheets integration using Apps Script
1. Open Google Sheets and create columns:
   - `Name`, `Phone`, `Requirement`, `Timestamp`
2. Open **Extensions → Apps Script**.
3. Paste this script:

```javascript
function doPost(e) {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
  var data = JSON.parse(e.postData.contents || "{}");

  sheet.appendRow([
    data.name || "",
    data.phone || "",
    data.requirement || "",
    data.timestamp || new Date().toISOString()
  ]);

  return ContentService
    .createTextOutput(JSON.stringify({ success: true }))
    .setMimeType(ContentService.MimeType.JSON);
}
```

4. Click **Deploy → New deployment**.
5. Type: **Web app**
   - Execute as: **Me**
   - Who has access: **Anyone**
6. Deploy and copy the Web App URL.
7. Paste URL into `config.json` as `google_script_url`.

## 3) Local preview
You can run any static server and open the site, for example:

```bash
python3 -m http.server 8080
```

Then visit `http://localhost:8080`.

## 4) GitHub Pages deployment
1. Create a GitHub repository.
2. Upload all files in this project root.
3. Go to **Settings → Pages**.
4. Under **Build and deployment**:
   - Source: **Deploy from a branch**
   - Branch: **main** (root)
5. Save and wait for deployment.
6. Your site will be live at:
   - `https://<your-username>.github.io/<repo-name>/`

## 5) How lead flow works
1. User submits form (name, phone, requirement).
2. Data is validated in browser.
3. Data POSTed to `google_script_url`.
4. Success message shown.
5. User redirected to WhatsApp with encoded bilingual prefilled message.

## Notes
- This project is serverless (no custom backend).
- Keep `config.json` updated to reuse this template for any coaching center.
