// @ts-nocheck
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import AOS from "aos";
import "aos/dist/aos.css";

(function () {
    "use strict";

    /**
     * Apply .scrolled class to the body as the page is scrolled down
     */
    function toggleScrolled() {
        const selectBody = document.querySelector("body");
        const selectHeader = document.querySelector("#header");
        if (!selectBody || !selectHeader) return;
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
        const body = document.querySelector("body");
        if (!body || !mobileNavToggleBtn) return;
        body.classList.toggle("mobile-nav-active");
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
    if (scrollTop) {
        scrollTop.addEventListener("click", (e) => {
            e.preventDefault();
            window.scrollTo({
                top: 0,
                behavior: "smooth",
            });
        });
    }

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
    aosInit();
    window.addEventListener("load", aosInit);

    /**
     * Initiate glightbox
     */
    if (typeof GLightbox !== "undefined") {
        GLightbox({
            selector: ".glightbox",
        });
    }

    /**
     * Init swiper sliders
     */
    function initSwiper() {
        if (typeof Swiper === "undefined") {
            return;
        }
        document.querySelectorAll(".init-swiper").forEach(function (swiperElement) {
            const configElement = swiperElement.querySelector(".swiper-config");
            if (!configElement) {
                return;
            }

            let config;
            try {
                config = JSON.parse(configElement.innerHTML.trim());
            } catch (error) {
                console.warn("Invalid swiper config JSON", error);
                return;
            }

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
    if (typeof PureCounter !== "undefined") {
        new PureCounter();
    }

    /*
     * Pricing Toggle
     */

    const pricingContainers = document.querySelectorAll(".pricing-toggle-container");

    pricingContainers.forEach(function (container) {
        const pricingSwitch = container.querySelector('.pricing-toggle input[type="checkbox"]');
        const monthlyText = container.querySelector(".monthly");
        const yearlyText = container.querySelector(".yearly");

        if (!pricingSwitch || !monthlyText || !yearlyText) {
            return;
        }

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
            faqItem.parentElement?.classList.toggle("faq-active");
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
                    if (!section) {
                        return;
                    }
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
            if (!(navmenulink instanceof HTMLAnchorElement)) return;
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

// ── Simple fields (learning, tags, prerequisites) ─────────────────────────
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

// ── Modules ───────────────────────────────────────────────────────────────
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

function setupRegisterAvailabilityValidation() {
    const form = document.getElementById("registerForm") || document.getElementById("googleRegisterForm");
    if (!form) {
        return;
    }

    const usernameInput = form.querySelector('input[name="username"]');
    const emailInput = form.querySelector('input[name="email"]');

    if (!usernameInput || !emailInput) {
        return;
    }

    let checkInProgress = false;

    const usernameFeedback = document.createElement("div");
    usernameFeedback.className = "form-text text-danger d-none";
    usernameInput.closest(".mb-3")?.appendChild(usernameFeedback);

    const emailFeedback = document.createElement("div");
    emailFeedback.className = "form-text text-danger d-none";
    emailInput.closest(".mb-3")?.appendChild(emailFeedback);

    const setFieldState = (input, feedback, hasError, message) => {
        if (hasError) {
            input.classList.add("is-invalid");
            input.setCustomValidity(message || "Valor no disponible");
            feedback.textContent = message || "Valor no disponible";
            feedback.classList.remove("d-none");
        } else {
            input.classList.remove("is-invalid");
            input.setCustomValidity("");
            feedback.textContent = "";
            feedback.classList.add("d-none");
        }
    };

    const checkAvailability = async () => {
        const username = usernameInput.value.trim();
        const email = emailInput.value.trim();

        if (!username || !email || checkInProgress) {
            return;
        }

        checkInProgress = true;

        try {
            const response = await fetch(`/register/check-availability?username=${encodeURIComponent(username)}&email=${encodeURIComponent(email)}`, { method: "GET", headers: { Accept: "application/json" } });

            if (!response.ok) {
                return;
            }

            const data = await response.json();
            setFieldState(usernameInput, usernameFeedback, data.usernameTaken, "Ese nombre de usuario ya está en uso");
            setFieldState(emailInput, emailFeedback, data.emailTaken, "Ese correo electrónico ya está registrado");
        } catch (_error) {
            // On network error, let backend validation handle it on submit
        } finally {
            checkInProgress = false;
        }
    };

    usernameInput.addEventListener("blur", checkAvailability);
    emailInput.addEventListener("blur", checkAvailability);

    form.addEventListener("submit", async (event) => {
        await checkAvailability();
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
            form.reportValidity();
        }
    });
}

// ── Agenda sessions ───────────────────────────────────────────────────────
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

// ── Speakers ──────────────────────────────────────────────────────────────
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

function escapeHtml(value) {
    return String(value || "")
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

function initAdminDashboard() {
    const adminSection = document.getElementById("admin-dashboard-section");
    if (!adminSection) {
        return;
    }

    const activeTabKey = adminSection.dataset.activeTab;
    const csrfToken = adminSection.dataset.csrfToken;
    const csrfParam = adminSection.dataset.csrfParam;

    const tabBtn = document.querySelector('#adminTabs [data-tab-key="' + activeTabKey + '"]');
    if (tabBtn) {
        new bootstrap.Tab(tabBtn).show();
    } else {
        const usersTab = document.getElementById("tab-users");
        if (usersTab) {
            new bootstrap.Tab(usersTab).show();
        }
    }

    const banConfirmModalEl = document.getElementById("banConfirmModal");
    const banConfirmModal = banConfirmModalEl ? new bootstrap.Modal(banConfirmModalEl) : null;
    const banConfirmUsername = document.getElementById("banConfirmUsername");
    const confirmBanBtn = document.getElementById("confirmBanBtn");
    let pendingBanForm = null;

    function openBanConfirmation(form, username) {
        if (!banConfirmModal || !form) {
            return true;
        }
        pendingBanForm = form;
        if (banConfirmUsername) {
            banConfirmUsername.textContent = username || "Usuario";
        }
        banConfirmModal.show();
        return false;
    }

    document.addEventListener("click", function (event) {
        const banButton = event.target.closest(".js-ban-confirm");
        if (!banButton) {
            return;
        }
        event.preventDefault();
        openBanConfirmation(banButton.form, banButton.dataset.username);
    });

    if (confirmBanBtn) {
        confirmBanBtn.addEventListener("click", function () {
            if (pendingBanForm) {
                pendingBanForm.submit();
            }
        });
    }

    if (banConfirmModalEl) {
        banConfirmModalEl.addEventListener("hidden.bs.modal", function () {
            pendingBanForm = null;
        });
    }

    const loadMoreUsersBtn = document.getElementById("loadMoreUsersBtn");
    if (loadMoreUsersBtn) {
        loadMoreUsersBtn.addEventListener("click", function () {
            const page = this.getAttribute("data-page");
            const query = this.getAttribute("data-query");
            const urlParams = new URLSearchParams();
            urlParams.set("page", page);
            if (query) urlParams.set("query", query);

            fetch("/admin/api/users?" + urlParams.toString())
                .then((response) => response.json())
                .then((data) => {
                    const usersList = document.getElementById("usersList");

                    if (data.length > 0) {
                        data.forEach((user) => {
                            const tr = document.createElement("tr");

                            const statusHtml = user.isActive ? '<span class="badge bg-success">Activo</span>' : '<span class="badge bg-danger">Baneado</span>';
                            const subscriptionHtml = user.isSubscribed ? '<span class="badge bg-success">Sí</span>' : '<span class="badge bg-secondary">No</span>';

                            let actionHtml = "";
                            if (user.isActive) {
                                const safeUsername = escapeHtml(user.username);
                                actionHtml = `
                                <form action="/new/admin/users/${user.id}/ban" method="post" class="d-flex align-items-start">
                                    <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                                    <button type="submit" class="btn btn-sm btn-accent-outline js-ban-confirm" data-username="${safeUsername}">
                                        <i class="bi bi-slash-circle"></i> Banear
                                    </button>
                                </form>`;
                            } else {
                                actionHtml = `
                                <form action="/new/admin/users/${user.id}/unban" method="post" class="d-flex align-items-start">
                                    <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                                    <button type="submit" class="btn btn-sm btn-accent">
                                        <i class="bi bi-check-circle"></i> Desbanear
                                    </button>
                                </form>`;
                            }

                            tr.innerHTML = `
                                <td>
                                    <div class="d-flex align-items-center gap-2">
                                        <i class="bi bi-person-circle fs-4 text-muted"></i>
                                        <span class="fw-semibold">${escapeHtml(user.username)}</span>
                                    </div>
                                </td>
                                <td class="text-muted">${escapeHtml(user.email)}</td>
                                <td>${subscriptionHtml}</td>
                                <td>${statusHtml}</td>
                                <td class="text-end">
                                    <div class="d-flex justify-content-end align-items-start gap-2 flex-nowrap">
                                        <a href="/new/profile/${user.id}" class="btn btn-sm btn-accent-outline">Ver perfil</a>
                                        ${actionHtml}
                                    </div>
                                </td>
                            `;
                            usersList.appendChild(tr);
                        });

                        this.setAttribute("data-page", parseInt(page, 10) + 1);

                        if (data.length < 10) {
                            this.style.display = "none";
                        }
                    } else {
                        this.style.display = "none";
                    }
                })
                .catch((error) => console.error("Error loading more users:", error));
        });
    }

    const loadMoreEventsBtn = document.getElementById("loadMoreEventsBtn");
    if (loadMoreEventsBtn) {
        loadMoreEventsBtn.addEventListener("click", function () {
            const page = this.getAttribute("data-page");
            const query = this.getAttribute("data-query");
            const urlParams = new URLSearchParams();
            urlParams.set("page", page);
            if (query) urlParams.set("query", query);

            fetch("/admin/api/events?" + urlParams.toString())
                .then((response) => response.json())
                .then((data) => {
                    const eventsList = document.getElementById("eventsList");

                    if (data.length > 0) {
                        data.forEach((event) => {
                            const tr = document.createElement("tr");
                            if (event.isPendingReview) {
                                tr.classList.add("table-warning");
                            }

                            const titleHtml = `
                                <div>
                                    <span class="fw-semibold">${escapeHtml(event.title)}</span>
                                    ${event.isPendingReview ? '<span class="badge bg-light text-accent ms-2">En revisión</span>' : ""}
                                    <p class="text-muted small mb-0">${escapeHtml(event.category)}</p>
                                </div>
                            `;

                            const statusHtml = event.isPendingReview ? '<span class="badge bg-light text-accent">Pendiente</span>' : `<span class="badge bg-secondary">${escapeHtml(event.status)}</span>`;

                            let actionHtml = `
                                <a href="/new/event/${event.id}" class="btn btn-sm btn-accent-outline">
                                    <i class="bi bi-eye"></i> Ver
                                </a>
                                <a href="/new/event/${event.id}/edit" class="btn btn-sm btn-accent-outline">
                                    <i class="bi bi-pencil"></i> Editar
                                </a>
                            `;

                            if (event.isPendingReview) {
                                actionHtml += `
                                <form action="/new/admin/events/${event.id}/approve" method="post" class="d-inline">
                                    <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                                    <button type="submit" class="btn btn-sm btn-accent">
                                        <i class="bi bi-check-lg"></i> Aprobar
                                    </button>
                                </form>
                                <form action="/new/admin/events/${event.id}/reject" method="post" class="d-inline">
                                    <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                                    <button type="submit" class="btn btn-sm btn-accent-outline" onclick="return confirm('¿Rechazar este evento?')">
                                        <i class="bi bi-x-lg"></i> Rechazar
                                    </button>
                                </form>
                                `;
                            }

                            tr.innerHTML = `
                                <td>${titleHtml}</td>
                                <td class="text-muted">${escapeHtml(event.creatorUsername)}</td>
                                <td>${statusHtml}</td>
                                <td class="text-end">
                                    <div class="d-flex justify-content-end align-items-start gap-2 flex-wrap">
                                        ${actionHtml}
                                    </div>
                                </td>
                            `;

                            eventsList.appendChild(tr);
                        });

                        this.setAttribute("data-page", parseInt(page, 10) + 1);

                        if (data.length < 10) {
                            this.parentElement.style.display = "none";
                        }
                    } else {
                        this.parentElement.style.display = "none";
                    }
                })
                .catch((error) => console.error("Error cargando más eventos:", error));
        });
    }

    const loadMoreCoursesBtn = document.getElementById("loadMoreCoursesBtn");
    if (loadMoreCoursesBtn) {
        loadMoreCoursesBtn.addEventListener("click", function () {
            const page = this.getAttribute("data-page");
            const query = this.getAttribute("data-query");
            const urlParams = new URLSearchParams();
            urlParams.set("page", page);
            if (query) urlParams.set("query", query);

            fetch("/admin/api/courses?" + urlParams.toString())
                .then((response) => response.json())
                .then((data) => {
                    const coursesList = document.getElementById("coursesList");

                    if (data.length > 0) {
                        data.forEach((course) => {
                            const tr = document.createElement("tr");
                            if (course.isPendingReview) {
                                tr.classList.add("table-warning");
                            }

                            const titleHtml = `
                                <div>
                                    <span class="fw-semibold">${escapeHtml(course.title)}</span>
                                    ${course.isPendingReview ? '<span class="badge bg-light text-accent ms-2">En revisión</span>' : ""}
                                    <p class="text-muted small mb-0">${escapeHtml(course.shortDescription)}</p>
                                </div>
                            `;

                            const statusHtml = course.isPendingReview ? '<span class="badge bg-light text-accent">Pendiente</span>' : `<span class="badge bg-secondary">${escapeHtml(course.status)}</span>`;

                            let actionHtml = `
                                <a href="/new/course/${course.id}" class="btn btn-sm btn-accent-outline">
                                    <i class="bi bi-eye"></i> Ver
                                </a>
                                <a href="/new/course/${course.id}/edit" class="btn btn-sm btn-accent-outline">
                                    <i class="bi bi-pencil"></i> Editar
                                </a>
                            `;

                            if (course.isPendingReview) {
                                actionHtml += `
                                <form action="/new/admin/courses/${course.id}/approve" method="post" class="d-inline">
                                    <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                                    <button type="submit" class="btn btn-sm btn-accent">
                                        <i class="bi bi-check-lg"></i> Aprobar
                                    </button>
                                </form>
                                <form action="/new/admin/courses/${course.id}/reject" method="post" class="d-inline">
                                    <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                                    <button type="submit" class="btn btn-sm btn-accent-outline" onclick="return confirm('¿Rechazar este curso?')">
                                        <i class="bi bi-x-lg"></i> Rechazar
                                    </button>
                                </form>
                                `;
                            }

                            tr.innerHTML = `
                                <td>${titleHtml}</td>
                                <td class="text-muted">${escapeHtml(course.creatorUsername)}</td>
                                <td>${statusHtml}</td>
                                <td class="text-end">
                                    <div class="d-flex justify-content-end align-items-start gap-2 flex-wrap">
                                        ${actionHtml}
                                    </div>
                                </td>
                            `;

                            coursesList.appendChild(tr);
                        });

                        this.setAttribute("data-page", parseInt(page, 10) + 1);

                        if (data.length < 10) {
                            this.parentElement.style.display = "none";
                        }
                    } else {
                        this.parentElement.style.display = "none";
                    }
                })
                .catch((error) => console.error("Error cargando más cursos:", error));
        });
    }

    const loadMoreOrdersBtn = document.getElementById("loadMoreOrdersBtn");
    if (loadMoreOrdersBtn) {
        loadMoreOrdersBtn.addEventListener("click", function () {
            const page = this.getAttribute("data-page");
            const urlParams = new URLSearchParams();
            urlParams.set("page", page);

            fetch("/admin/api/orders?" + urlParams.toString())
                .then((response) => response.json())
                .then((data) => {
                    const ordersList = document.getElementById("ordersList");

                    if (data.length > 0) {
                        data.forEach((order) => {
                            const tr = document.createElement("tr");

                            const userHtml = order.username ? escapeHtml(order.username) : "-";
                            const clientHtml = `
                                <div>${escapeHtml(order.billingFullName)}</div>
                                <small class="text-muted">${escapeHtml(order.billingEmail)}</small>
                            `;

                            const dateHtml = order.paidAt ? escapeHtml(order.paidAt) : order.createdAt ? escapeHtml(order.createdAt) : "";

                            const paymentHtml = `
                                <div>${escapeHtml(order.paymentMethod)}</div>
                                <small class="text-muted">${escapeHtml(order.paymentReference)}</small>
                            `;

                            tr.innerHTML = `
                                <td class="fw-semibold">#${order.id}</td>
                                <td>${userHtml}</td>
                                <td>${clientHtml}</td>
                                <td><span class="badge bg-secondary">${escapeHtml(order.status)}</span></td>
                                <td>${dateHtml}</td>
                                <td>${paymentHtml}</td>
                                <td class="text-end fw-semibold">${escapeHtml(order.totalAmountEuros)} €</td>
                            `;

                            ordersList.appendChild(tr);
                        });

                        this.setAttribute("data-page", parseInt(page, 10) + 1);

                        if (data.length < 10) {
                            this.parentElement.style.display = "none";
                        }
                    } else {
                        this.parentElement.style.display = "none";
                    }
                })
                .catch((error) => console.error("Error cargando más pedidos:", error));
        });
    }
}

function loadExternalScriptOnce(src, scriptId, integrity, crossOrigin) {
    return new Promise((resolve, reject) => {
        const existingById = scriptId ? document.getElementById(scriptId) : null;
        const existingBySrc = Array.from(document.querySelectorAll("script[src]")).find((script) => script.src === src);
        const existing = existingById || existingBySrc;

        if (existing) {
            if (existing.dataset.loaded === "true") {
                resolve();
                return;
            }
            existing.addEventListener("load", () => resolve(), { once: true });
            existing.addEventListener("error", () => reject(new Error(`No se pudo cargar ${src}`)), { once: true });
            return;
        }

        const script = document.createElement("script");
        script.src = src;
        if (scriptId) {
            script.id = scriptId;
        }
        if (integrity) {
            script.integrity = integrity;
        }
        if (crossOrigin) {
            script.crossOrigin = crossOrigin;
        }

        script.async = true;
        script.addEventListener(
            "load",
            () => {
                script.dataset.loaded = "true";
                resolve();
            },
            { once: true },
        );
        script.addEventListener("error", () => reject(new Error(`No se pudo cargar ${src}`)), { once: true });
        document.head.appendChild(script);
    });
}

function initBootstrapValidationForms() {
    const forms = document.querySelectorAll(".needs-validation");
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener(
            "submit",
            function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                    const alert = document.getElementById("clientValidationAlert");
                    if (alert) {
                        alert.classList.remove("d-none");
                        alert.scrollIntoView({ behavior: "smooth", block: "center" });
                    }
                }
                form.classList.add("was-validated");
            },
            false,
        );
    });
}

function initProgressBars() {
    document.querySelectorAll(".course-progress-fill[data-progress]").forEach(function (bar) {
        var value = parseInt(bar.getAttribute("data-progress"), 10);
        if (isNaN(value)) value = 0;
        value = Math.max(0, Math.min(100, value));
        bar.style.width = value + "%";
    });
}

function setProfileEditMode(isEditing) {
    var main = document.getElementById("profile-page-main");
    var view = document.getElementById("profile-view");
    var edit = document.getElementById("profile-edit");
    var about = document.getElementById("profile-about-container");
    var mainContainer = document.getElementById("profile-main-container");

    if (!view || !edit) return;

    if (isEditing) {
        view.style.display = "none";
        edit.style.display = "block";
        if (about) about.style.display = "none";
        if (mainContainer) {
            mainContainer.classList.remove("col-lg-4");
            mainContainer.classList.add("col-12", "is-editing");
        }
        if (main) {
            main.classList.add("profile-edit-mode");
        }
    } else {
        view.style.display = "block";
        edit.style.display = "none";
        if (about) about.style.display = "block";
        if (mainContainer) {
            mainContainer.classList.remove("col-12", "is-editing");
            mainContainer.classList.add("col-lg-4");
        }
        if (main) {
            main.classList.remove("profile-edit-mode");
        }
    }
}

function toggleEdit() {
    var view = document.getElementById("profile-view");
    if (!view) return;
    if (view.style.display === "none") {
        setProfileEditMode(false);
    } else {
        setProfileEditMode(true);
    }
}

function previewImage(event) {
    var reader = new FileReader();
    reader.onload = function () {
        var preview = document.getElementById("preview-img");
        if (preview) {
            preview.src = reader.result;
        }
    };
    if (event.target.files && event.target.files[0]) {
        reader.readAsDataURL(event.target.files[0]);
    }
}

function initProfilePage() {
    if (!document.getElementById("profile-page-main")) {
        return;
    }
    if (document.querySelector(".alert.alert-warning")) {
        setProfileEditMode(true);
    }

    var countrySelect = document.getElementById("country");
    if (countrySelect) {
        var currentCountry = countrySelect.getAttribute("data-current-country");
        if (currentCountry) {
            for (var i = 0; i < countrySelect.options.length; i++) {
                if (countrySelect.options[i].value === currentCountry) {
                    countrySelect.selectedIndex = i;
                    break;
                }
            }
        }
    }
}

function initCourseDetailsPage() {
    var progressBar = document.getElementById("courseProgressBar");
    var progressLabel = document.getElementById("courseProgressLabel");

    if (progressBar) {
        var initialValue = parseInt(progressBar.getAttribute("data-progress"), 10);
        if (isNaN(initialValue)) initialValue = 0;
        initialValue = Math.max(0, Math.min(100, initialValue));
        progressBar.style.width = initialValue + "%";
        if (progressLabel) {
            progressLabel.textContent = initialValue + "%";
        }
    }

    document.querySelectorAll(".complete-lesson-form").forEach(function (form) {
        form.addEventListener("submit", async function (event) {
            event.preventDefault();

            var submitButton = form.querySelector('button[type="submit"]');
            if (!submitButton) return;

            var originalText = submitButton.textContent;
            submitButton.disabled = true;
            submitButton.textContent = "Guardando...";

            try {
                var csrfInput = form.querySelector('input[name="_csrf"]');
                var headers = {
                    "X-Requested-With": "XMLHttpRequest",
                };

                if (csrfInput && csrfInput.value) {
                    headers["X-CSRF-TOKEN"] = csrfInput.value;
                }

                var response = await fetch(form.action, {
                    method: "POST",
                    headers: headers,
                });

                if (!response.ok) {
                    throw new Error("No se pudo completar la lección");
                }

                var data = await response.json();

                var completedBadge = document.createElement("span");
                completedBadge.className = "badge bg-success";
                completedBadge.textContent = "Completada";

                form.replaceWith(completedBadge);

                if (progressBar && progressLabel && typeof data.progressPercentage === "number") {
                    var nextValue = Math.max(0, Math.min(100, data.progressPercentage));
                    progressBar.style.width = nextValue + "%";
                    progressBar.setAttribute("data-progress", String(nextValue));
                    progressLabel.textContent = nextValue + "%";
                }
            } catch (_error) {
                submitButton.disabled = false;
                submitButton.textContent = originalText;
                alert("No se pudo guardar el progreso. Inténtalo de nuevo.");
            }
        });
    });
}

async function initChoicesOnElement(element) {
    if (!element || typeof Choices !== "undefined") {
        if (element && typeof Choices !== "undefined") {
            new Choices(element, {
                removeItemButton: true,
                searchEnabled: true,
                searchPlaceholderValue: "Buscar etiquetas...",
                placeholderValue: "Selecciona etiquetas para buscar",
                noResultsText: "No se encontraron etiquetas",
                itemSelectText: "Presiona para seleccionar",
            });
        }
        return;
    }

    await loadExternalScriptOnce("https://cdn.jsdelivr.net/npm/choices.js/public/assets/scripts/choices.min.js", "choices-js-cdn");

    if (typeof Choices !== "undefined") {
        new Choices(element, {
            removeItemButton: true,
            searchEnabled: true,
            searchPlaceholderValue: "Buscar etiquetas...",
            placeholderValue: "Selecciona etiquetas para buscar",
            noResultsText: "No se encontraron etiquetas",
            itemSelectText: "Presiona para seleccionar",
        });
    }
}

function initLoadMoreCoursesCatalog() {
    const loadMoreBtn = document.getElementById("loadMoreBtn");
    if (!loadMoreBtn) {
        return;
    }

    loadMoreBtn.addEventListener("click", function () {
        const page = this.getAttribute("data-page");
        const urlParams = new URLSearchParams(window.location.search);
        urlParams.set("page", page);

        fetch("/api/courses?" + urlParams.toString())
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                if (!Array.isArray(data)) {
                    throw new Error("Respuesta inválida al cargar más cursos");
                }

                const courseList = document.getElementById("courseList");

                if (data.length > 0) {
                    data.forEach((course) => {
                        const article = document.createElement("article");
                        article.className = "course-card-full";

                        let tagsHtml = "";
                        if (course.tags) {
                            course.tags.forEach((tag) => {
                                tagsHtml += `<span class="course-tag">${tag.name}</span>`;
                            });
                        }

                        let subscribedHtml = "";
                        if (course.isSubscribed) {
                            subscribedHtml = '<span class="badge bg-success">Ya suscrito</span>';
                        }

                        article.innerHTML = `
                                    <div class="course-card-header">
                                        <div>
                                            <h3 class="course-card-title">${course.title}</h3>
                                            <div class="course-tags">
                                                ${tagsHtml}
                                            </div>
                                        </div>
                                        <div class="d-flex flex-column align-items-end gap-1">
                                            ${subscribedHtml}
                                            <div class="course-card-price">${course.priceInEuros}€</div>
                                        </div>
                                    </div>
                                    <p class="course-card-desc">${course.description}</p>
                                    <div class="course-card-meta">
                                        <div class="course-rating">
                                            <i class="bi bi-star-fill course-rating-star"></i>
                                            <span class="course-rating-value">${course.averageRating}</span>
                                            <span class="course-rating-count">(${course.ratingCount})</span>
                                        </div>
                                        <div class="course-card-students"><i class="bi bi-people"></i>${course.subscribersNumber} suscritos</div>
                                        <div class="course-card-students"><i class="bi bi-person-circle"></i> Por ${course.creatorUsername}</div>
                                    </div>
                                    <div class="course-card-actions">
                                        <a href="/new/course/${course.id}" class="btn btn-outline-primary btn-sm btn-accent-outline course-card-btn">Ver curso</a>
                                    </div>
                                `;
                        courseList.appendChild(article);
                    });

                    this.setAttribute("data-page", parseInt(page, 10) + 1);

                    if (data.length < 10) {
                        this.style.display = "none";
                    }
                } else {
                    this.style.display = "none";
                }
            })
            .catch((error) => console.error("Error loading more courses:", error));
    });
}

function initLoadMoreEventsCatalog() {
    const loadMoreBtn = document.getElementById("loadMoreEventsBtn");
    const eventsList = document.getElementById("eventsList");

    if (!loadMoreBtn || !eventsList || document.getElementById("admin-dashboard-section")) {
        return;
    }

    loadMoreBtn.addEventListener("click", function () {
        const page = this.getAttribute("data-page");
        const urlParams = new URLSearchParams(window.location.search);
        urlParams.set("page", page);

        fetch("/api/events?" + urlParams.toString())
            .then((response) => response.json())
            .then((data) => {
                if (data.length > 0) {
                    data.forEach((event) => {
                        const article = document.createElement("article");
                        article.className = "course-card-full";
                        article.setAttribute("data-aos", "fade-up");
                        article.setAttribute("data-aos-delay", "50");

                        let tagsHtml = "";
                        if (event.tags && event.tags.length > 0) {
                            event.tags.forEach((t) => {
                                tagsHtml += `<span class="course-tag">${t.name}</span>`;
                            });
                        }

                        let isSubscribedHtml = event.isSubscribed ? '<span class="badge bg-success">Entrada comprada</span>' : "";

                        let locationHtml = event.isLocation ? `<div><i class="bi bi-geo-alt"></i> ${event.locationName}</div>` : `<div><i class="bi bi-geo-alt"></i> Online</div>`;

                        article.innerHTML = `
                                <div class="course-card-header">
                                    <div>
                                        <h3 class="course-card-title">${event.title}</h3>
                                        <div class="course-tags">
                                            ${tagsHtml}
                                        </div>
                                    </div>
                                    <div class="d-flex flex-column align-items-end gap-1">
                                        ${isSubscribedHtml}
                                        <div class="course-card-price">${event.priceEuros}€</div>
                                    </div>
                                </div>
                                <p class="course-card-desc">${event.description}</p>
                                <div class="course-card-meta">
                                    <div><i class="bi bi-calendar-event"></i> ${event.formattedDate}</div>
                                    <div><i class="bi bi-clock"></i> ${event.formattedTime}</div>
                                    ${locationHtml}
                                </div>
                                <div class="course-card-actions">
                                    <a href="/new/event/${event.id}" class="btn btn-outline-primary btn-sm btn-accent-outline course-card-btn">Ver detalles</a>
                                </div>
                            `;

                        eventsList.appendChild(article);
                    });

                    this.setAttribute("data-page", parseInt(page, 10) + 1);
                    if (data.length < 10) {
                        this.style.display = "none";
                    }
                } else {
                    this.style.display = "none";
                }
            })
            .catch((error) => console.error("Error loading more events:", error));
    });
}

function initLanguageSelectPrefill() {
    const languageSelect = document.getElementById("language");
    if (!languageSelect) {
        return;
    }

    const selectedLanguage = languageSelect.getAttribute("data-selected-language");
    if (selectedLanguage) {
        languageSelect.value = selectedLanguage;
    }
}

async function ensureLeafletLoaded() {
    if (typeof L !== "undefined") {
        return;
    }

    await loadExternalScriptOnce("https://unpkg.com/leaflet@1.9.4/dist/leaflet.js", "leaflet-js-cdn", "sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=", "anonymous");
}

function initEventLocationSearch() {
    const locationInput = document.getElementById("eventLocation");
    const addressInput = document.getElementById("locationAddress");
    const cityInput = document.getElementById("locationCity");
    const countryInput = document.getElementById("locationCountry");
    const latInput = document.getElementById("locationLat");
    const lonInput = document.getElementById("locationLon");
    const resultsContainer = document.getElementById("search-results-name");
    const mapPreview = document.getElementById("map-preview");

    if (!locationInput || !addressInput || !cityInput || !countryInput || !latInput || !lonInput || !resultsContainer || !mapPreview) {
        return;
    }

    var map = null;
    var marker = null;
    var searchTimeout = null;
    var locationSearchEndpoint = "/api/v1/events/locations";

    function initMap(lat, lon) {
        if (typeof L === "undefined") {
            return;
        }

        if (!map) {
            mapPreview.innerHTML = "";
            map = L.map("map-preview").setView([lat, lon], 15);
            L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
                attribution: "&copy; OpenStreetMap contributors",
            }).addTo(map);
            marker = L.marker([lat, lon]).addTo(map);
        } else {
            const pos = [lat, lon];
            map.setView(pos, 15);
            marker.setLatLng(pos);
        }
    }

    const initialLat = parseFloat(latInput.value);
    const initialLon = parseFloat(lonInput.value);
    if (!isNaN(initialLat) && !isNaN(initialLon)) {
        initMap(initialLat, initialLon);
    }

    function handleSelection(result) {
        const address = result.address || {};
        locationInput.value = result.display_name.split(",")[0];
        addressInput.value = address.road || address.pedestrian || result.display_name;
        cityInput.value = address.city || address.town || address.village || "";
        countryInput.value = address.country || "";
        latInput.value = result.lat;
        lonInput.value = result.lon;

        resultsContainer.classList.add("d-none");
        initMap(result.lat, result.lon);
    }

    locationInput.addEventListener("input", function () {
        clearTimeout(searchTimeout);
        const query = this.value.trim();
        if (query.length < 3) {
            resultsContainer.classList.add("d-none");
            return;
        }

        searchTimeout = setTimeout(() => {
            fetch(`${locationSearchEndpoint}?q=${encodeURIComponent(query)}`)
                .then((response) => {
                    if (!response.ok) {
                        throw new Error(`Location API respondió con estado ${response.status}`);
                    }
                    return response.json();
                })
                .then((data) => {
                    resultsContainer.innerHTML = "";
                    if (data.length > 0) {
                        data.forEach((item) => {
                            const div = document.createElement("div");
                            div.className = "search-result-item";
                            div.textContent = item.display_name;
                            div.addEventListener("click", () => handleSelection(item));
                            resultsContainer.appendChild(div);
                        });
                        resultsContainer.classList.remove("d-none");
                    } else {
                        resultsContainer.classList.add("d-none");
                    }
                })
                .catch((_error) => {
                    resultsContainer.classList.add("d-none");
                });
        }, 500);
    });

    document.addEventListener("click", function (e) {
        if (e.target !== locationInput) {
            resultsContainer.classList.add("d-none");
        }
    });
}

async function initEventDetailsMap() {
    const mapElement = document.getElementById("map");
    if (!mapElement) {
        return;
    }

    await ensureLeafletLoaded();

    if (typeof L === "undefined") {
        return;
    }

    var lat = parseFloat(mapElement.dataset.lat);
    var lon = parseFloat(mapElement.dataset.lon);
    var locationName = mapElement.dataset.name || "";

    if (Number.isNaN(lat) || Number.isNaN(lon)) {
        return;
    }

    var map = L.map("map").setView([lat, lon], 13);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map);

    L.marker([lat, lon]).addTo(map).bindPopup(locationName).openPopup();
}

function initCartCheckoutValidation() {
    const form = document.getElementById("checkoutForm");
    const cardNumber = document.getElementById("cardNumber");
    const cardExpiry = document.getElementById("cardExpiry");
    const cardCvv = document.getElementById("cardCvv");

    if (!form || !cardNumber || !cardExpiry || !cardCvv) {
        return;
    }

    cardNumber.addEventListener("input", function () {
        let v = this.value.replace(/\D/g, "").substring(0, 16);
        this.value = v.replace(/(.{4})/g, "$1 ").trim();
    });

    cardExpiry.addEventListener("input", function () {
        let v = this.value.replace(/\D/g, "").substring(0, 4);
        if (v.length >= 3) {
            v = v.substring(0, 2) + "/" + v.substring(2);
        }
        this.value = v;
    });

    cardCvv.addEventListener("input", function () {
        this.value = this.value.replace(/\D/g, "").substring(0, 3);
    });

    form.addEventListener("submit", function (e) {
        let valid = true;

        form.querySelectorAll(".form-control").forEach(function (el) {
            el.classList.remove("is-invalid");
        });

        const cardName = document.getElementById("cardName");
        if (!cardName || !cardName.value.trim()) {
            if (cardName) cardName.classList.add("is-invalid");
            valid = false;
        }

        var email = document.getElementById("billingEmail");
        if (!email || !email.value.trim() || !email.value.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
            if (email) email.classList.add("is-invalid");
            valid = false;
        }

        var digits = cardNumber.value.replace(/\D/g, "");
        if (digits.length !== 16) {
            cardNumber.classList.add("is-invalid");
            valid = false;
        }

        var expiryMatch = cardExpiry.value.match(/^(0[1-9]|1[0-2])\/(\d{2})$/);
        if (!expiryMatch) {
            cardExpiry.classList.add("is-invalid");
            valid = false;
        } else {
            var expMonth = parseInt(expiryMatch[1], 10);
            var expYear = 2000 + parseInt(expiryMatch[2], 10);
            var now = new Date();
            var currentMonth = now.getMonth() + 1;
            var currentYear = now.getFullYear();
            if (expYear < currentYear || (expYear === currentYear && expMonth < currentMonth)) {
                cardExpiry.classList.add("is-invalid");
                valid = false;
            }
        }

        if (!/^\d{3}$/.test(cardCvv.value)) {
            cardCvv.classList.add("is-invalid");
            valid = false;
        }

        if (!valid) {
            e.preventDefault();
        }

        if (valid) {
            cardNumber.value = digits;
        }
    });
}

async function initGenericChartPage() {
    const canvas = document.getElementById("genericChart");
    const labelsContainer = document.getElementById("chartLabelsData");
    const valuesContainer = document.getElementById("chartValuesData");

    if (!canvas || !labelsContainer || !valuesContainer) {
        return;
    }

    if (typeof Chart === "undefined") {
        await loadExternalScriptOnce("https://cdn.jsdelivr.net/npm/chart.js", "chart-js-cdn");
    }

    if (typeof Chart === "undefined") {
        return;
    }

    var labels = Array.from(labelsContainer.querySelectorAll("li")).map((li) => li.textContent || "");
    var values = Array.from(valuesContainer.querySelectorAll("li")).map((li) => parseFloat(li.textContent || "0") || 0);
    var chartType = canvas.dataset.chartType || "bar";
    var chartTitle = canvas.dataset.chartTitle || "";

    var colors = [
        "rgba(217, 109, 60, 0.8)",
        "rgba(133, 97, 61, 0.8)",
        "rgba(66, 40, 35, 0.8)",
        "rgba(199, 167, 119, 0.8)",
        "rgba(243, 228, 201, 0.8)",
        "rgba(180, 90, 50, 0.8)",
        "rgba(160, 118, 75, 0.8)",
        "rgba(100, 60, 50, 0.8)",
        "rgba(235, 175, 125, 0.8)",
        "rgba(255, 240, 215, 0.8)",
    ];
    var borderColors = colors.map(function (c) {
        return c.replace("0.8", "1");
    });

    new Chart(canvas, {
        type: chartType,
        data: {
            labels: labels,
            datasets: [
                {
                    label: chartTitle,
                    data: values,
                    backgroundColor: colors.slice(0, values.length),
                    borderColor: borderColors.slice(0, values.length),
                    borderWidth: 2,
                },
            ],
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: "bottom", labels: { boxWidth: 12, padding: 10, font: { size: 11 } } },
                title: { display: false },
            },
            layout: { padding: 5 },
        },
    });
}

function initCatalogTagFilters() {
    const tagFilters = document.getElementById("tagFilters");
    const tagNames = document.getElementById("tagNames");

    if (tagFilters) {
        initChoicesOnElement(tagFilters).then(() => {
            tagFilters.addEventListener("change", function () {
                if (this.form) {
                    this.form.submit();
                }
            });
        });
    }

    if (tagNames) {
        initChoicesOnElement(tagNames);
    }
}

async function initEventCreationEditionPage() {
    if (!document.getElementById("eventLocation") && !document.getElementById("map")) {
        return;
    }

    try {
        await ensureLeafletLoaded();
    } catch (_error) {
        console.warn("No se pudo cargar Leaflet; se mantiene el autocompletado sin mapa.");
    }

    initEventLocationSearch();
}

window.toggleEdit = toggleEdit;
window.previewImage = previewImage;
Object.assign(window, {
    addDynamicItem,
    addLesson,
    removeLesson,
    removeModule,
    addModule,
    addAgendaItem,
    addSpeaker,
});

document.addEventListener("DOMContentLoaded", () => {
    refreshModuleBindings();
    updateModuleCount();
    setupRegisterAvailabilityValidation();
    initBootstrapValidationForms();
    initProgressBars();
    initProfilePage();
    initCourseDetailsPage();
    initCatalogTagFilters();
    initLoadMoreCoursesCatalog();
    initLoadMoreEventsCatalog();
    initLanguageSelectPrefill();
    initEventCreationEditionPage();
    initEventDetailsMap();
    initCartCheckoutValidation();
    initGenericChartPage();
    initAdminDashboard();
});

export {};
