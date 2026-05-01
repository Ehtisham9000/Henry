// Main app state
let config = null;
let currentLang = "en";

const byId = (id) => document.getElementById(id);
const t = (obj) => (obj && obj[currentLang]) || "";

async function loadConfig() {
  const res = await fetch("config.json", { cache: "no-cache" });
  if (!res.ok) throw new Error("Unable to load config.json");
  return res.json();
}

function renderNav() {
  const nav = byId("mainNav");
  const labels = config.language_content.nav;
  nav.innerHTML = `
    <a href="#subjects">${t(labels.subjects)}</a>
    <a href="#timings">${t(labels.timings)}</a>
    <a href="#fees">${t(labels.fees)}</a>
    <a href="#about">${t(labels.about)}</a>
    <a href="#contact">${t(labels.contact)}</a>
  `;
}

function renderCards(listEl, items, isSimple = false) {
  listEl.innerHTML = "";
  items.forEach((item) => {
    const card = document.createElement("article");
    card.className = "card";
    if (isSimple) {
      card.innerHTML = `<h3>${t(item)}</h3>`;
    } else {
      card.innerHTML = `<h3>${t(item.title)}</h3><p>${t(item.value)}</p>`;
    }
    listEl.appendChild(card);
  });
}

function renderContent() {
  const c = config.language_content;

  document.documentElement.lang = currentLang === "ur" ? "ur" : "en";
  document.documentElement.dir = currentLang === "ur" ? "rtl" : "ltr";

  byId("brandName").textContent = config.center_name;
  byId("heroTitle").textContent = t(c.title);
  byId("heroTagline").textContent = t(c.tagline);
  byId("heroCardTitle").textContent = t(c.hero_card_title);
  byId("heroCardText").textContent = t(c.hero_card_text);
  byId("heroCta").textContent = t(c.hero_cta);
  byId("heroWhatsapp").textContent = t(c.hero_whatsapp);

  byId("subjectsHeading").textContent = t(c.headings.subjects);
  byId("timingsHeading").textContent = t(c.headings.timings);
  byId("feesHeading").textContent = t(c.headings.fees);
  byId("aboutHeading").textContent = t(c.headings.about);
  byId("contactHeading").textContent = t(c.headings.contact);
  byId("formHeading").textContent = t(c.headings.form);

  byId("aboutText").textContent = t(c.about);
  byId("contactText").textContent = t(c.contact_text);

  byId("callBtn").textContent = t(c.buttons.call);
  byId("waHeaderBtn").textContent = t(c.buttons.whatsapp);
  byId("contactCallBtn").textContent = t(c.buttons.call);
  byId("contactWaBtn").textContent = t(c.buttons.whatsapp);
  byId("submitBtn").textContent = t(c.buttons.submit);

  byId("nameLabel").textContent = t(c.form_labels.name);
  byId("phoneLabel").textContent = t(c.form_labels.phone);
  byId("requirementLabel").textContent = t(c.form_labels.requirement);
  byId("footerText").textContent = t(c.footer);

  byId("callBtn").href = `tel:+${config.phone_number}`;
  byId("contactCallBtn").href = `tel:+${config.phone_number}`;

  const waBase = `https://wa.me/${config.whatsapp_number}`;
  byId("waHeaderBtn").href = waBase;
  byId("heroWhatsapp").href = waBase;
  byId("contactWaBtn").href = waBase;

  byId("langToggle").textContent = currentLang === "en" ? "اردو" : "English";

  renderNav();
  renderCards(byId("subjectsList"), config.subjects, true);
  renderCards(byId("timingsList"), config.timings);
  renderCards(byId("feesList"), config.fees);
}

function setupForm() {
  const form = byId("leadForm");
  const statusEl = byId("formStatus");
  const submitBtn = byId("submitBtn");

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const name = byId("nameInput").value.trim();
    const phone = byId("phoneInput").value.trim();
    const requirement = byId("requirementInput").value.trim();

    if (!name || !phone || !requirement || !/^\d+$/.test(phone)) {
      statusEl.textContent = t(config.language_content.messages.validation);
      statusEl.style.color = "#b91c1c";
      return;
    }

    const payload = { name, phone, requirement, timestamp: new Date().toISOString() };

    try {
      submitBtn.disabled = true;
      statusEl.textContent = t(config.language_content.messages.loading);
      statusEl.style.color = "#1e63d7";

      const res = await fetch(config.google_script_url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error("Request failed");

      statusEl.textContent = t(config.language_content.messages.success);
      statusEl.style.color = "#15803d";
      form.reset();

      const waTemplate = t(config.language_content.wa_template)
        .replace("{name}", name)
        .replace("{phone}", phone)
        .replace("{requirement}", requirement);
      const waUrl = `https://wa.me/${config.whatsapp_number}?text=${encodeURIComponent(waTemplate)}`;

      setTimeout(() => {
        window.location.href = waUrl;
      }, 900);
    } catch (error) {
      statusEl.textContent = t(config.language_content.messages.error);
      statusEl.style.color = "#b91c1c";
      console.error(error);
    } finally {
      submitBtn.disabled = false;
    }
  });
}

function setupAnimations() {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) entry.target.classList.add("visible");
      });
    },
    { threshold: 0.12 }
  );

  document.querySelectorAll(".fade-in").forEach((el) => observer.observe(el));
}

async function init() {
  try {
    config = await loadConfig();
    renderContent();
    setupForm();
    setupAnimations();

    byId("langToggle").addEventListener("click", () => {
      currentLang = currentLang === "en" ? "ur" : "en";
      renderContent();
    });
  } catch (error) {
    console.error(error);
    document.body.innerHTML = "<p style='padding:24px'>Failed to load site configuration.</p>";
  }
}

init();
