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

document.addEventListener("DOMContentLoaded", () => {
    refreshModuleBindings();
    updateModuleCount();
});

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

document.addEventListener("DOMContentLoaded", () => {
    setupRegisterAvailabilityValidation();
});

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

                            let actionHtml = "";
                            if (user.isActive) {
                                const safeUsername = escapeHtml(user.username);
                                actionHtml = `
                                <form action="/admin/users/${user.id}/ban" method="post" class="d-flex align-items-start">
                                    <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                                    <button type="submit" class="btn btn-sm btn-accent-outline js-ban-confirm" data-username="${safeUsername}">
                                        <i class="bi bi-slash-circle"></i> Banear
                                    </button>
                                </form>`;
                            } else {
                                actionHtml = `
                                <form action="/admin/users/${user.id}/unban" method="post" class="d-flex align-items-start">
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
                                <td>${statusHtml}</td>
                                <td class="text-end">
                                    <div class="d-flex justify-content-end align-items-start gap-2 flex-nowrap">
                                        <a href="/profile/${user.id}" class="btn btn-sm btn-accent-outline">Ver perfil</a>
                                        ${actionHtml}
                                    </div>
                                </td>
                            `;
                            usersList.appendChild(tr);
                        });

                        this.setAttribute("data-page", parseInt(page, 10) + 1);

                        if (data.length < 5) {
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
                                <a href="/event/${event.id}" class="btn btn-sm btn-accent-outline">
                                    <i class="bi bi-eye"></i> Ver
                                </a>
                                <a href="/event/${event.id}/edit" class="btn btn-sm btn-accent-outline">
                                    <i class="bi bi-pencil"></i> Editar
                                </a>
                            `;

                            if (event.isPendingReview) {
                                actionHtml += `
                                <form action="/admin/events/${event.id}/approve" method="post" class="d-inline">
                                    <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                                    <button type="submit" class="btn btn-sm btn-accent">
                                        <i class="bi bi-check-lg"></i> Aprobar
                                    </button>
                                </form>
                                <form action="/admin/events/${event.id}/reject" method="post" class="d-inline">
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

                        if (data.length < 5) {
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
                                <a href="/course/${course.id}" class="btn btn-sm btn-accent-outline">
                                    <i class="bi bi-eye"></i> Ver
                                </a>
                                <a href="/course/${course.id}/edit" class="btn btn-sm btn-accent-outline">
                                    <i class="bi bi-pencil"></i> Editar
                                </a>
                            `;

                            if (course.isPendingReview) {
                                actionHtml += `
                                <form action="/admin/courses/${course.id}/approve" method="post" class="d-inline">
                                    <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                                    <button type="submit" class="btn btn-sm btn-accent">
                                        <i class="bi bi-check-lg"></i> Aprobar
                                    </button>
                                </form>
                                <form action="/admin/courses/${course.id}/reject" method="post" class="d-inline">
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

                        if (data.length < 5) {
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

                        if (data.length < 5) {
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

document.addEventListener("DOMContentLoaded", () => {
    initAdminDashboard();
});
