import type { FormEvent } from "react";
import { useState } from "react";
import { Link, redirect, useNavigate, useSearchParams } from "react-router";

import { login as loginRequest } from "~/services/authService";
import { loadGlobalDataIntoStore } from "~/services/globalService";

export async function clientLoader() {
  const globalData = await loadGlobalDataIntoStore();
  if (globalData?.isUserLoggedIn) {
    return redirect("/new");
  }
  return null;
}

clientLoader.hydrate = true;

export default function LoginRoute() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    try {
      setSubmitting(true);
      setError(null);

      const response = await loginRequest({ username: username.trim(), password });
      if (response.status !== "SUCCESS") {
        throw new Error(response.message || "No se pudo iniciar sesión.");
      }

      await loadGlobalDataIntoStore(true);

      const redirectTo = searchParams.get("redirectTo");
      navigate(redirectTo && redirectTo.startsWith("/new") ? redirectTo : "/new", { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error al iniciar sesión.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="main">
      <section className="section">
        <div className="container">
          <div className="row g-0 shadow-lg rounded-4 overflow-hidden" style={{ minHeight: 600 }}>
            <div className="col-lg-6 d-none d-lg-block position-relative">
              <img src="/services/Services-3.webp" alt="Join SCAM" className="img-fluid w-100 h-100" style={{ objectFit: "cover", objectPosition: "center" }} />
              <div className="position-absolute top-0 start-0 w-100 h-100" style={{ background: "rgba(0, 0, 0, 0.3)" }} />
              <div className="position-absolute bottom-0 start-0 p-5 text-white">
                <h3 className="fw-bold text-white">Unete a nuestra comunidad</h3>
                <p className="lead mb-0">Aprende de los mejores expertos y transforma tu carrera profesional hoy mismo.</p>
              </div>
            </div>

            <div className="col-lg-6 bg-white d-flex align-items-center">
              <div className="p-5 w-100">
                <div className="text-center mb-4">
                  <h2 className="fw-bold text-dark">Iniciar Sesion</h2>
                  <p className="text-muted">Bienvenido de nuevo a SCAM</p>
                </div>

                {error && (
                  <div className="alert alert-danger" role="alert">
                    {error}
                  </div>
                )}

                <form id="loginForm" onSubmit={onSubmit}>
                  <div className="mb-3">
                    <label htmlFor="loginEmail" className="form-label small fw-bold">
                      Usuario o Correo electronico
                    </label>
                    <div className="input-group">
                      <span className="input-group-text">
                        <i className="bi bi-person" />
                      </span>
                      <input
                        type="text"
                        className="form-control"
                        id="loginEmail"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        placeholder="Usuario o correo electronico"
                        autoComplete="username"
                      />
                    </div>
                  </div>

                  <div className="mb-4">
                    <label htmlFor="loginPassword" className="form-label small fw-bold">
                      Contrasena
                    </label>
                    <div className="input-group">
                      <span className="input-group-text">
                        <i className="bi bi-lock" />
                      </span>
                      <input
                        type="password"
                        className="form-control"
                        id="loginPassword"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        placeholder="********"
                        autoComplete="current-password"
                      />
                    </div>
                  </div>

                  <div className="d-grid mb-3">
                    <button type="submit" className="btn btn-primary py-2 fw-bold" disabled={submitting}>
                      {submitting ? "Iniciando..." : "Iniciar Sesion"}
                    </button>
                  </div>
                </form>

                <div className="text-center p-3 rounded bg-light">
                  <p className="mb-0 small">
                    No tienes cuenta?{" "}
                    <Link to="/new/register" className="fw-bold text-decoration-none" style={{ color: "var(--accent-color)" }}>
                      Registrarse
                    </Link>
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}