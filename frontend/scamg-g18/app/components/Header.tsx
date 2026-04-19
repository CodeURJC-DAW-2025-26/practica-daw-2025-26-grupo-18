import { useEffect } from "react";
import { Link, NavLink } from "react-router";
import { Container, Dropdown, Navbar } from "react-bootstrap";
import { useGlobalStore } from "~/stores/globalStore";

export default function Header() {
    const globalData = useGlobalStore().globalData;
    const fetchGlobalData = useGlobalStore().fetchGlobalData;

    useEffect(() => {
        void fetchGlobalData();
    }, [fetchGlobalData]);

    const isUserLoggedIn = globalData?.isUserLoggedIn ?? false;
    const isAdmin = globalData?.isAdmin ?? false;
    const userId = globalData?.userId;
    const userName = globalData?.userName ?? "Usuario";
    const userProfileImage = globalData?.userProfileImage ?? "/default_avatar.png";

    return (
        <Navbar id="header" className="header d-flex align-items-center sticky-top">
            <Container className="position-relative d-flex align-items-center justify-content-between">
                <Navbar.Brand as={Link} to="/" className="logo d-flex align-items-center me-auto me-xl-0">
                    <img src="/logo.png" alt="SCAM" style={{ height: 36 }} />
                    <h1>SCAM</h1>
                </Navbar.Brand>

                <nav id="navmenu" className="navmenu">
                    <ul>
                        <li>
                            <NavLink to="/" end className={({ isActive }) => (isActive ? "active" : "")}>
                                Inicio
                            </NavLink>
                        </li>
                        <li>
                            <NavLink to="/courses" className={({ isActive }) => (isActive ? "active" : "")}>
                                Cursos
                            </NavLink>
                        </li>
                        {isUserLoggedIn && (
                            <li>
                                <NavLink to="/courses/subscribed" className={({ isActive }) => (isActive ? "active" : "")}>
                                    Cursos suscritos
                                </NavLink>
                            </li>
                        )}
                        <li>
                            <NavLink to="/events" className={({ isActive }) => (isActive ? "active" : "")}>
                                Eventos
                            </NavLink>
                        </li>
                        {isUserLoggedIn && (
                            <li>
                                <NavLink to="/events/purchased" className={({ isActive }) => (isActive ? "active" : "")}>
                                    Eventos comprados
                                </NavLink>
                            </li>
                        )}
                        <li>
                            <a href="/#pricing">Pricing</a>
                        </li>
                        {isAdmin && (
                            <li>
                                <NavLink to="/admin" className={({ isActive }) => (isActive ? "active" : "")}>
                                    Admin Dashboard
                                </NavLink>
                            </li>
                        )}
                    </ul>
                    <i className="mobile-nav-toggle d-xl-none bi bi-list" />
                </nav>

                <div className="header-user-actions d-flex align-items-center gap-3">
                    <Link to="/cart" className="header-cart-link" aria-label="Ir al carrito">
                        <i className="bi bi-bag" />
                    </Link>

                    {isUserLoggedIn ? (
                        <Dropdown align="end" className="header-profile dropdown">
                            <Dropdown.Toggle as="a" id="profileDropdown" href="#" className="d-flex align-items-center gap-3 text-decoration-none dropdown-toggle" style={{ fontFamily: "var(--nav-font)", color: "var(--nav-color)", fontSize: "16px" }}>
                                <span className="profile-name">{userName}</span>
                                <img src={userProfileImage} alt="Profile" className="rounded-circle" style={{ width: 40, height: 40, objectFit: "cover" }} />
                            </Dropdown.Toggle>

                            <Dropdown.Menu className="dropdown-menu-end profile-dropdown" aria-labelledby="profileDropdown">
                                <Dropdown.Item href={userId ? `/profile/${userId}` : "/profile/me"}>
                                    <i className="bi bi-person-circle" /> Mi Perfil
                                </Dropdown.Item>
                                <Dropdown.Divider />
                                <form action="/logout" method="post" className="m-0">
                                    <button type="submit" className="dropdown-item">
                                        <i className="bi bi-box-arrow-right" /> Logout
                                    </button>
                                </form>
                            </Dropdown.Menu>
                        </Dropdown>
                    ) : (
                        <a href="/login" className="btn-getstarted">
                            Iniciar sesión/Registrarse
                        </a>
                    )}
                </div>
            </Container>
        </Navbar>
    );
}
