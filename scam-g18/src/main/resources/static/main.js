(function () {
    "use strict";

    /**
     * Apply .scrolled class to the body as the page is scrolled down
     */
    function toggleScrolled() {
        const selectBody = document.querySelector("body");
        const selectHeader = document.querySelector("#header");
        if (!selectHeader.classList.contains("scroll-up-sticky") && !selectHeader.classList.contains("sticky-top") && !selectHeader.classList.contains("fixed-top")) return;
        window.scrollY > 100 ? selectBody.classList.add("scrolled") : selectBody.classList.remove("scrolled");
    }

    document.addEventListener("scroll", toggleScrolled);
    window.addEventListener("load", toggleScrolled);

    /**
     * Mobile nav toggle
     */
    const mobileNavToggleBtn = document.querySelector(".mobile-nav-toggle");

    function mobileNavToogle() {
        document.querySelector("body").classList.toggle("mobile-nav-active");
        mobileNavToggleBtn.classList.toggle("bi-list");
        mobileNavToggleBtn.classList.toggle("bi-x");
    }
    if (mobileNavToggleBtn) {
        mobileNavToggleBtn.addEventListener("click", mobileNavToogle);
    }

    /**
     * Hide mobile nav on same-page/hash links
     */
    document.querySelectorAll("#navmenu a").forEach((navmenu) => {
        navmenu.addEventListener("click", () => {
            if (document.querySelector(".mobile-nav-active")) {
                mobileNavToogle();
            }
        });
    });

    /**
     * Toggle mobile nav dropdowns
     */
    document.querySelectorAll(".navmenu .toggle-dropdown").forEach((navmenu) => {
        navmenu.addEventListener("click", function (e) {
            e.preventDefault();
            this.parentNode.classList.toggle("active");
            this.parentNode.nextElementSibling.classList.toggle("dropdown-active");
            e.stopImmediatePropagation();
        });
    });

    /**
     * Scroll top button
     */
    let scrollTop = document.querySelector(".scroll-top");

    function toggleScrollTop() {
        if (scrollTop) {
            window.scrollY > 100 ? scrollTop.classList.add("active") : scrollTop.classList.remove("active");
        }
    }
    scrollTop.addEventListener("click", (e) => {
        e.preventDefault();
        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });
    });

    window.addEventListener("load", toggleScrollTop);
    document.addEventListener("scroll", toggleScrollTop);

    /**
     * Animation on scroll function and init
     */
    function aosInit() {
        AOS.init({
            duration: 600,
            easing: "ease-in-out",
            once: true,
            mirror: false,
        });
    }
    window.addEventListener("load", aosInit);

    /**
     * Initiate glightbox
     */
    const glightbox = GLightbox({
        selector: ".glightbox",
    });

    /**
     * Init swiper sliders
     */
    function initSwiper() {
        document.querySelectorAll(".init-swiper").forEach(function (swiperElement) {
            let config = JSON.parse(swiperElement.querySelector(".swiper-config").innerHTML.trim());

            if (swiperElement.classList.contains("swiper-tab")) {
                initSwiperWithCustomPagination(swiperElement, config);
            } else {
                new Swiper(swiperElement, config);
            }
        });
    }

    window.addEventListener("load", initSwiper);

    /**
     * Initiate Pure Counter
     */
    new PureCounter();

    /*
     * Pricing Toggle
     */

    const pricingContainers = document.querySelectorAll(".pricing-toggle-container");

    pricingContainers.forEach(function (container) {
        const pricingSwitch = container.querySelector('.pricing-toggle input[type="checkbox"]');
        const monthlyText = container.querySelector(".monthly");
        const yearlyText = container.querySelector(".yearly");

        pricingSwitch.addEventListener("change", function () {
            const pricingItems = container.querySelectorAll(".pricing-item");

            if (this.checked) {
                monthlyText.classList.remove("active");
                yearlyText.classList.add("active");
                pricingItems.forEach((item) => {
                    item.classList.add("yearly-active");
                });
            } else {
                monthlyText.classList.add("active");
                yearlyText.classList.remove("active");
                pricingItems.forEach((item) => {
                    item.classList.remove("yearly-active");
                });
            }
        });
    });

    /**
     * Frequently Asked Questions Toggle
     */
    document.querySelectorAll(".faq-item h3, .faq-item .faq-toggle, .faq-item .faq-header").forEach((faqItem) => {
        faqItem.addEventListener("click", () => {
            faqItem.parentNode.classList.toggle("faq-active");
        });
    });

    /**
     * Correct scrolling position upon page load for URLs containing hash links.
     */
    window.addEventListener("load", function (e) {
        if (window.location.hash) {
            if (document.querySelector(window.location.hash)) {
                setTimeout(() => {
                    let section = document.querySelector(window.location.hash);
                    let scrollMarginTop = getComputedStyle(section).scrollMarginTop;
                    window.scrollTo({
                        top: section.offsetTop - parseInt(scrollMarginTop),
                        behavior: "smooth",
                    });
                }, 100);
            }
        }
    });

    /**
     * Navmenu Scrollspy
     */
    let navmenulinks = document.querySelectorAll(".navmenu a");

    function navmenuScrollspy() {
        navmenulinks.forEach((navmenulink) => {
            if (!navmenulink.hash) return;
            let section = document.querySelector(navmenulink.hash);
            if (!section) return;
            let position = window.scrollY + 200;
            if (position >= section.offsetTop && position <= section.offsetTop + section.offsetHeight) {
                document.querySelectorAll(".navmenu a.active").forEach((link) => link.classList.remove("active"));
                navmenulink.classList.add("active");
            } else {
                navmenulink.classList.remove("active");
            }
        });
    }
    window.addEventListener("load", navmenuScrollspy);
    document.addEventListener("scroll", navmenuScrollspy);
})();

// ── Campos simples (aprendizajes, etiquetas, requisitos) ──────────────────
function addDynamicItem(containerId, placeholder, icon) {
    const container = document.getElementById(containerId);
    const nameMap = {
        "learning-container": "learningPoints",
        "topics-container": "tagNames",
        "prerequisites-container": "prerequisites",
    };
    const inputName = nameMap[containerId] || "item";

    const div = document.createElement("div");
    div.className = "input-group mb-2";
    div.innerHTML = `
        <span class="input-group-text"><i class="bi ${icon}"></i></span>
        <input type="text" class="form-control" name="${inputName}" placeholder="${placeholder}" required>
        <button class="btn btn-delete" type="button" onclick="this.parentElement.remove()">×</button>`;
    container.appendChild(div);
}

// ── Módulos ───────────────────────────────────────────────────────────────
function updateModuleCount() {
    const count = document.querySelectorAll("#modules-container .module-item").length;
    const el = document.getElementById("moduleCount");
    if (el) el.textContent = count;
}

function refreshModuleBindings() {
    const moduleItems = document.querySelectorAll("#modules-container .module-item");

    moduleItems.forEach((moduleItem, moduleIndex) => {
        const moduleTitleInput = moduleItem.querySelector('input[data-field="module-title"]');
        const moduleDescriptionInput = moduleItem.querySelector('input[data-field="module-description"]');

        if (moduleTitleInput) {
            moduleTitleInput.name = `modules[${moduleIndex}].title`;
        }
        if (moduleDescriptionInput) {
            moduleDescriptionInput.name = `modules[${moduleIndex}].description`;
        }

        const lessonRows = moduleItem.querySelectorAll(".lessons-list .input-group");
        lessonRows.forEach((lessonRow, lessonIndex) => {
            const lessonTitleInput = lessonRow.querySelector('input[data-field="lesson-title"]');
            const lessonUrlInput = lessonRow.querySelector('input[data-field="lesson-url"]');

            if (lessonTitleInput) {
                lessonTitleInput.name = `modules[${moduleIndex}].lessons[${lessonIndex}].title`;
            }
            if (lessonUrlInput) {
                lessonUrlInput.name = `modules[${moduleIndex}].lessons[${lessonIndex}].videoUrl`;
            }
        });
    });
}

function addLesson(btn) {
    const moduleItem = btn.closest(".module-item");
    const lessonsList = moduleItem.querySelector(".lessons-list");
    const div = document.createElement("div");
    div.className = "input-group input-group-sm mb-2";
    div.innerHTML = `
        <span class="input-group-text bg-white"><i class="bi bi-play-circle"></i></span>
        <input type="text" class="form-control" data-field="lesson-title" placeholder="Título de la lección" required>
        <input type="text" class="form-control" data-field="lesson-url" placeholder="Enlace del vídeo (URL)" required>
        <button class="btn btn-delete" type="button" onclick="removeLesson(this)">×</button>`;
    lessonsList.appendChild(div);
    refreshModuleBindings();
}

function removeLesson(btn) {
    btn.parentElement.remove();
    refreshModuleBindings();
}

function removeModule(btn) {
    btn.closest(".module-item").remove();
    updateModuleCount();
    refreshModuleBindings();
}

function addModule() {
    const container = document.getElementById("modules-container");
    const count = container.querySelectorAll(".module-item").length + 1;

    const div = document.createElement("div");
    div.className = "border rounded p-3 mb-3 module-item bg-light";
    div.innerHTML = `
        <div class="d-flex justify-content-between mb-3 align-items-center">
            <div class="flex-grow-1 me-3">
                <input type="text" class="form-control fw-bold mb-2" data-field="module-title"
                    placeholder="Título del Módulo" value="Módulo ${count}: " required>
                <input type="text" class="form-control text-muted" data-field="module-description"
                    placeholder="Breve descripción del módulo" required>
            </div>
            <button type="button" class="btn btn-sm btn-delete" onclick="removeModule(this)">Eliminar Módulo</button>
        </div>
        <div class="lessons-container ms-3 border-start ps-3">
            <label class="form-label small text-muted mb-2">Lecciones</label>
            <div class="lessons-list">
                <div class="input-group input-group-sm mb-2">
                    <span class="input-group-text bg-white"><i class="bi bi-play-circle"></i></span>
                    <input type="text" class="form-control" data-field="lesson-title" placeholder="Título de la lección" required>
                    <input type="text" class="form-control" data-field="lesson-url" placeholder="Enlace del vídeo (URL)" required>
                    <button class="btn btn-delete" type="button" onclick="removeLesson(this)">×</button>
                </div>
            </div>
            <button type="button" class="btn btn-sm btn-add mt-2" onclick="addLesson(this)">+ Añadir Lección</button>
        </div>`;
    container.appendChild(div);
    updateModuleCount();
    refreshModuleBindings();
}

document.addEventListener("DOMContentLoaded", refreshModuleBindings);

// ── Sesiones de agenda ────────────────────────────────────────────────────
function addAgendaItem() {
    const container = document.getElementById("agenda-container");
    const count = container.querySelectorAll(".agenda-item").length + 1;

    const div = document.createElement("div");
    div.className = "border rounded p-3 mb-3 agenda-item bg-light";
    div.innerHTML = `
        <div class="d-flex justify-content-between mb-2">
            <div class="fw-bold">Sesión ${count}</div>
            <button type="button" class="btn btn-sm btn-delete"
                onclick="this.closest('.agenda-item').remove()">Eliminar</button>
        </div>
        <div class="row g-2">
            <div class="col-md-3">
                <input type="time" class="form-control" name="sessionTimes" required>
            </div>
            <div class="col-md-9">
                <input type="text" class="form-control" name="sessionTitles"
                    placeholder="Título de la sesión" required>
            </div>
            <div class="col-12">
                <textarea class="form-control" rows="2" name="sessionDescriptions"
                    placeholder="Descripción de la sesión"></textarea>
            </div>
        </div>`;
    container.appendChild(div);
}

// ── Ponentes ──────────────────────────────────────────────────────────────
function addSpeaker() {
    const container = document.getElementById("speakers-container");
    const count = container.querySelectorAll(".speaker-item").length + 1;

    const div = document.createElement("div");
    div.className = "border rounded p-3 mb-3 speaker-item bg-light";
    div.innerHTML = `
        <div class="d-flex justify-content-between mb-2">
            <div class="fw-bold">Ponente ${count}</div>
            <button type="button" class="btn btn-sm btn-delete"
                onclick="this.closest('.speaker-item').remove()">Eliminar</button>
        </div>
        <div class="row g-2">
            <div class="col-md-12">
                <input type="text" class="form-control" name="speakerNames"
                    placeholder="Nombre completo y Cargo" required>
            </div>
        </div>`;
    container.appendChild(div);
}
