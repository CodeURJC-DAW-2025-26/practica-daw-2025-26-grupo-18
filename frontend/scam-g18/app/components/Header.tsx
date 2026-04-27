import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router";
import { Container, Dropdown, Navbar } from "react-bootstrap";
import { useGlobalStore } from "~/stores/globalStore";
import { useAuthStore } from "~/stores/authStore";
import { logout } from "~/services/authService";

export default function Header() {
    const navigate = useNavigate();
    const user = useAuthStore((state) => state.user);
    const isLoggedIn = useAuthStore((state) => state.isLoggedIn());
    const isAdmin = useGlobalStore((state) => state.globalData?.isAdmin ?? false);
    const [loggingOut, setLoggingOut] = useState(false);

    const userName = user?.username ?? "Usuario";
    const userProfileImage = user?.profileImage ?? "/services/default_avatar.png";
    const userId = user?.id;

    async function handleLogout() {
        try {
            setLoggingOut(true);
            await logout();
        } catch {
        } finally {
            setLoggingOut(false);
            navigate("/new/login", { replace: true });
        }
    }

    return (
        <Navbar id="header" className="header d-flex align-items-center sticky-top">
            <Container className="position-relative d-flex align-items-center justify-content-between">
                <Navbar.Brand as={Link} to="/new" className="logo d-flex align-items-center me-auto me-xl-0">
                    <img src="/logo.png" alt="SCAM" style={{ height: 36 }} />
                    <h1>SCAM</h1>
                </Navbar.Brand>

                <nav id="navmenu" className="navmenu">
                    <ul>
                        <li>
                            <NavLink to="/new" end className={({ isActive }) => (isActive ? "active" : "")}>
                                Inicio
                            </NavLink>
                        </li>
                        <li>
                            <NavLink to="/new/courses" className={({ isActive }) => (isActive ? "active" : "")}>
                                Cursos
                            </NavLink>
                        </li>
                        {isLoggedIn && (
                            <li>
                                <NavLink to="/new/courses/subscribed" className={({ isActive }) => (isActive ? "active" : "")}>
                                    Cursos suscritos
                                </NavLink>
                            </li>
                        )}
                        <li>
                            <NavLink to="/new/events" className={({ isActive }) => (isActive ? "active" : "")}>
                                Eventos
                            </NavLink>
                        </li>
                        {isLoggedIn && (
                            <li>
                                <NavLink to="/new/events/purchased" className={({ isActive }) => (isActive ? "active" : "")}>
                                    Eventos comprados
                                </NavLink>
                            </li>
                        )}
                        <li>
                            <a href="/new#pricing">Pricing</a>
                        </li>
                        {isAdmin && (
                            <li>
                                <NavLink to="/new/admin" className={({ isActive }) => (isActive ? "active" : "")}>
                                    Admin Dashboard
                                </NavLink>
                            </li>
                        )}
                    </ul>
                    <i className="mobile-nav-toggle d-xl-none bi bi-list" />
                </nav>

                <div className="header-user-actions d-flex align-items-center gap-3">
                    <Link to="/new/cart" className="header-cart-link" aria-label="Ir al carrito">
                        <i className="bi bi-bag" />
                    </Link>

                    {isLoggedIn ? (
                        <Dropdown align="end" className="header-profile dropdown">
                            <Dropdown.Toggle as="a" id="profileDropdown" className="d-flex align-items-center gap-3 text-decoration-none dropdown-toggle" style={{ fontFamily: "var(--nav-font)", color: "var(--nav-color)", fontSize: "16px" }}>
                                <span className="profile-name">{userName}</span>
                                <img src={userProfileImage} alt="Profile" className="rounded-circle" style={{ width: 40, height: 40, objectFit: "cover" }} />
                            </Dropdown.Toggle>

                            <Dropdown.Menu className="dropdown-menu-end profile-dropdown" aria-labelledby="profileDropdown">
                                <Dropdown.Item href={userId ? `/new/profile/${userId}` : "/new/profile/me"}>
                                    <i className="bi bi-person-circle" /> Mi Perfil
                                </Dropdown.Item>
                                <Dropdown.Divider />
                                <button type="button" className="dropdown-item" onClick={() => void handleLogout()} disabled={loggingOut}>
                                    <i className="bi bi-box-arrow-right" /> {loggingOut ? "Cerrando..." : "Logout"}
                                </button>
                            </Dropdown.Menu>
                        </Dropdown>
                    ) : (
                        <a href="/new/login" className="btn-getstarted">
                            Iniciar sesión/Registrarse
                        </a>
                    )}
                </div>
            </Container>
        </Navbar>
    );
}
