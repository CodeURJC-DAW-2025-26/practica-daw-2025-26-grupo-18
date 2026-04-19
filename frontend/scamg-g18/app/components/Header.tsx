import { useEffect } from "react";
import { Link, NavLink } from "react-router";
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
    <header id="header" className="header d-flex align-items-center sticky-top">
      <div className="container position-relative d-flex align-items-center justify-content-between">
        <Link to="/" className="logo d-flex align-items-center me-auto me-xl-0">
          <img src="/logo.png" alt="SCAM" />
          <h1>SCAM</h1>
        </Link>

        <nav id="navmenu" className="navmenu">
          <ul>
            <li>
              <NavLink to="/" end className={({ isActive }) => (isActive ? "active" : "")}>Inicio</NavLink>
            </li>
            <li>
              <NavLink to="/courses" className={({ isActive }) => (isActive ? "active" : "")}>Cursos</NavLink>
            </li>
            {isUserLoggedIn && (
              <li>
                <NavLink to="/courses/subscribed" className={({ isActive }) => (isActive ? "active" : "")}>Cursos suscritos</NavLink>
              </li>
            )}
            <li>
              <NavLink to="/events" className={({ isActive }) => (isActive ? "active" : "")}>Eventos</NavLink>
            </li>
            {isUserLoggedIn && (
              <li>
                <NavLink to="/events/purchased" className={({ isActive }) => (isActive ? "active" : "")}>Eventos comprados</NavLink>
              </li>
            )}
            <li><a href="/#pricing">Pricing</a></li>
            {isAdmin && (
              <li>
                <NavLink to="/admin" className={({ isActive }) => (isActive ? "active" : "")}>Admin Dashboard</NavLink>
              </li>
            )}
          </ul>
          <i className="mobile-nav-toggle d-xl-none bi bi-list"></i>
        </nav>

        <div className="header-user-actions d-flex align-items-center gap-3">
          <Link to="/cart" className="header-cart-link" aria-label="Ir al carrito">
            <i className="bi bi-bag"></i>
          </Link>

          {isUserLoggedIn ? (
            <div className="d-flex align-items-center gap-2">
              <Link to={userId ? `/profile/${userId}` : "/profile/me"} className="d-flex align-items-center gap-2 text-decoration-none" style={{ color: "var(--nav-color)" }}>
                <span>{userName}</span>
                <img src={userProfileImage} alt="Profile" className="rounded-circle" style={{ width: 40, height: 40, objectFit: "cover" }} />
              </Link>
              <a className="btn-getstarted" href="/logout">Logout</a>
            </div>
          ) : (
            <Link className="btn-getstarted" to="/login">Iniciar sesión/Registrarse</Link>
          )}
        </div>
      </div>
    </header>
  );
}
