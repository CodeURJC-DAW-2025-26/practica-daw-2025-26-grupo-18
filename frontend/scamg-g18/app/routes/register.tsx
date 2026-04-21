import type { FormEvent } from "react";
import { useMemo, useState } from "react";
import { Link, redirect, useNavigate } from "react-router";

import { register as registerRequest } from "~/services/authService";
import { loadGlobalDataIntoStore } from "~/services/globalService";

export async function clientLoader() {
  const globalData = await loadGlobalDataIntoStore();
  if (globalData?.isUserLoggedIn) {
    return redirect("/new");
  }
  return null;
}

clientLoader.hydrate = true;

type RegisterFormState = {
  username: string;
  email: string;
  password: string;
  gender: "" | "MALE" | "FEMALE" | "PREFER_NOT_TO_SAY";
  birthDate: string;
  country: string;
  image?: File;
};

const COUNTRIES = [
  "España",
  "México",
  "Argentina",
  "Colombia",
  "Perú",
  "Chile",
  "Ecuador",
  "Venezuela",
  "Uruguay",
  "Paraguay",
  "Bolivia",
  "República Dominicana",
  "Costa Rica",
  "Panamá",
  "Guatemala",
  "Honduras",
  "El Salvador",
  "Nicaragua",
  "Cuba",
  "Puerto Rico",
];

export default function RegisterRoute() {
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState<RegisterFormState>({
    username: "",
    email: "",
    password: "",
    gender: "",
    birthDate: "",
    country: "",
  });

  const sortedCountries = useMemo(() => [...COUNTRIES].sort((a, b) => a.localeCompare(b, "es")), []);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!form.gender) {
      setError("Debes seleccionar un genero.");
      return;
    }

    try {
      setSubmitting(true);
      setError(null);

      const response = await registerRequest({
        username: form.username.trim(),
        email: form.email.trim(),
        password: form.password,
        gender: form.gender,
        birthDate: form.birthDate,
        country: form.country,
        image: form.image,
      });

      if (response.status !== "SUCCESS") {
        throw new Error(response.message || "No se pudo completar el registro.");
      }

      const globalData = await loadGlobalDataIntoStore(true);
      if (globalData?.isUserLoggedIn) {
        navigate("/new/profile/me", { replace: true });
      } else {
        navigate("/new/login", { replace: true });
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error al registrarte.");
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
                  <h2 className="fw-bold text-dark">Crear Cuenta</h2>
                  <p className="text-muted">Empieza tu aprendizaje hoy</p>
                </div>

                {error && (
                  <div className="alert alert-danger" role="alert">
                    {error}
                  </div>
                )}

                <form id="registerForm" onSubmit={onSubmit} encType="multipart/form-data" className="needs-validation" noValidate>
                  <div className="mb-3">
                    <label htmlFor="regUsername" className="form-label small fw-bold">
                      Nombre de usuario
                    </label>
                    <div className="input-group">
                      <span className="input-group-text">
                        <i className="bi bi-person" />
                      </span>
                      <input
                        type="text"
                        className="form-control"
                        id="regUsername"
                        value={form.username}
                        onChange={(event) => setForm((prev) => ({ ...prev, username: event.target.value }))}
                        placeholder="Tu nombre de usuario"
                        required
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regEmail" className="form-label small fw-bold">
                      Correo electronico
                    </label>
                    <div className="input-group">
                      <span className="input-group-text">
                        <i className="bi bi-envelope" />
                      </span>
                      <input
                        type="email"
                        className="form-control"
                        id="regEmail"
                        value={form.email}
                        onChange={(event) => setForm((prev) => ({ ...prev, email: event.target.value }))}
                        placeholder="nombre@ejemplo.com"
                        required
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regPassword" className="form-label small fw-bold">
                      Contrasena
                    </label>
                    <div className="input-group">
                      <span className="input-group-text">
                        <i className="bi bi-lock" />
                      </span>
                      <input
                        type="password"
                        className="form-control"
                        id="regPassword"
                        value={form.password}
                        onChange={(event) => setForm((prev) => ({ ...prev, password: event.target.value }))}
                        placeholder="********"
                        required
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regGender" className="form-label small fw-bold">
                      Genero
                    </label>
                    <div className="input-group">
                      <span className="input-group-text">
                        <i className="bi bi-gender-ambiguous" />
                      </span>
                      <select
                        className="form-select"
                        id="regGender"
                        value={form.gender}
                        onChange={(event) =>
                          setForm((prev) => ({ ...prev, gender: event.target.value as RegisterFormState["gender"] }))
                        }
                        required
                      >
                        <option value="" disabled>
                          Selecciona una opcion
                        </option>
                        <option value="MALE">Masculino</option>
                        <option value="FEMALE">Femenino</option>
                        <option value="PREFER_NOT_TO_SAY">Prefiero no decirlo</option>
                      </select>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regBirthDate" className="form-label small fw-bold">
                      Fecha de nacimiento
                    </label>
                    <div className="input-group">
                      <span className="input-group-text">
                        <i className="bi bi-calendar" />
                      </span>
                      <input
                        type="date"
                        className="form-control"
                        id="regBirthDate"
                        value={form.birthDate}
                        onChange={(event) => setForm((prev) => ({ ...prev, birthDate: event.target.value }))}
                        required
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regCountry" className="form-label small fw-bold">
                      Pais
                    </label>
                    <div className="input-group">
                      <span className="input-group-text">
                        <i className="bi bi-globe" />
                      </span>
                      <select
                        className="form-select"
                        id="regCountry"
                        value={form.country}
                        onChange={(event) => setForm((prev) => ({ ...prev, country: event.target.value }))}
                        required
                      >
                        <option value="" disabled>
                          Selecciona tu pais
                        </option>
                        {sortedCountries.map((country) => (
                          <option key={country} value={country}>
                            {country}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="mb-4">
                    <label htmlFor="regImage" className="form-label small fw-bold">
                      Foto de perfil <span className="text-muted fw-normal">(opcional)</span>
                    </label>
                    <div className="input-group">
                      <span className="input-group-text">
                        <i className="bi bi-image" />
                      </span>
                      <input
                        type="file"
                        className="form-control"
                        id="regImage"
                        accept="image/*"
                        onChange={(event) =>
                          setForm((prev) => ({
                            ...prev,
                            image: event.target.files?.[0],
                          }))
                        }
                      />
                    </div>
                    <div className="form-text text-muted">Si no subes ninguna, se usara una imagen por defecto.</div>
                  </div>

                  <div className="d-grid mb-4">
                    <button type="submit" className="btn btn-primary py-2 fw-bold" disabled={submitting}>
                      {submitting ? "Registrando..." : "Registrarse"}
                    </button>
                  </div>
                </form>

                <div className="text-center p-3 rounded bg-light">
                  <p className="mb-0 small">
                    Ya tienes cuenta?{" "}
                    <Link to="/new/login" className="fw-bold text-decoration-none" style={{ color: "var(--accent-color)" }}>
                      Iniciar Sesion
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