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
  const [touched, setTouched] = useState<Record<string, boolean>>({});

  const sortedCountries = useMemo(() => [...COUNTRIES].sort((a, b) => a.localeCompare(b, "es")), []);

  // Validation functions
  const validateUsername = (v: string) => v.trim().length >= 3 && v.trim().length <= 20;
  const validateEmail = (v: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
  const validatePassword = (v: string) => /^(?=.*[0-9])(?=.*[A-Z])(?=\S+$).{8,}$/.test(v);
  const validateGender = (v: string) => !!v;
  const validateBirthDate = (v: string) => {
    if (!v) return false;
    const date = new Date(v);
    const now = new Date();
    const eighteenYearsAgo = new Date();
    eighteenYearsAgo.setFullYear(now.getFullYear() - 18);
    return date <= eighteenYearsAgo;
  };
  const validateCountry = (v: string) => !!v;

  const formErrors = {
    username: !validateUsername(form.username),
    email: !validateEmail(form.email),
    password: !validatePassword(form.password),
    gender: !validateGender(form.gender),
    birthDate: !validateBirthDate(form.birthDate),
    country: !validateCountry(form.country),
  };

  const isFormValid = Object.values(formErrors).every(e => !e);

  const handleFieldChange = (name: string, value: any) => {
    setForm(prev => ({ ...prev, [name]: value }));
    setTouched(prev => ({ ...prev, [name]: true }));
  };

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!isFormValid) return;

    try {
      setSubmitting(true);
      setError(null);

      const response = await registerRequest({
        username: form.username.trim(),
        email: form.email.trim(),
        password: form.password,
        gender: form.gender as any,
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

                <form id="registerForm" onSubmit={onSubmit} encType="multipart/form-data" noValidate>
                  <div className="mb-3">
                    <label htmlFor="regUsername" className="form-label small fw-bold">
                      Nombre de usuario
                    </label>
                    <div className="input-group has-validation">
                      <span className="input-group-text">
                        <i className="bi bi-person" />
                      </span>
                      <input
                        type="text"
                        className={`form-control ${formErrors.username ? 'is-invalid' : 'is-valid'}`}
                        id="regUsername"
                        value={form.username}
                        onChange={(event) => handleFieldChange("username", event.target.value)}
                        placeholder="Tu nombre de usuario"
                        required
                      />
                      <div className="invalid-feedback">El usuario debe tener entre 3 y 20 caracteres.</div>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regEmail" className="form-label small fw-bold">
                      Correo electronico
                    </label>
                    <div className="input-group has-validation">
                      <span className="input-group-text">
                        <i className="bi bi-envelope" />
                      </span>
                      <input
                        type="email"
                        className={`form-control ${formErrors.email ? 'is-invalid' : 'is-valid'}`}
                        id="regEmail"
                        value={form.email}
                        onChange={(event) => handleFieldChange("email", event.target.value)}
                        placeholder="nombre@ejemplo.com"
                        required
                      />
                      <div className="invalid-feedback">Introduce un correo electrónico válido.</div>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regPassword" className="form-label small fw-bold">
                      Contrasena
                    </label>
                    <div className="input-group has-validation">
                      <span className="input-group-text">
                        <i className="bi bi-lock" />
                      </span>
                      <input
                        type="password"
                        className={`form-control ${formErrors.password ? 'is-invalid' : 'is-valid'}`}
                        id="regPassword"
                        value={form.password}
                        onChange={(event) => handleFieldChange("password", event.target.value)}
                        placeholder="********"
                        required
                      />
                      <div className="invalid-feedback">Mín. 8 caracteres: 1 mayúscula, 1 número y 7 letras.</div>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regGender" className="form-label small fw-bold">
                      Genero
                    </label>
                    <div className="input-group has-validation">
                      <span className="input-group-text">
                        <i className="bi bi-gender-ambiguous" />
                      </span>
                      <select
                        className={`form-select ${formErrors.gender ? 'is-invalid' : 'is-valid'}`}
                        id="regGender"
                        value={form.gender}
                        onChange={(event) => handleFieldChange("gender", event.target.value)}
                        required
                      >
                        <option value="" disabled>
                          Selecciona una opcion
                        </option>
                        <option value="MALE">Masculino</option>
                        <option value="FEMALE">Femenino</option>
                        <option value="PREFER_NOT_TO_SAY">Prefiero no decirlo</option>
                      </select>
                      <div className="invalid-feedback">Selecciona un género.</div>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regBirthDate" className="form-label small fw-bold">
                      Fecha de nacimiento
                    </label>
                    <div className="input-group has-validation">
                      <span className="input-group-text">
                        <i className="bi bi-calendar" />
                      </span>
                      <input
                        type="date"
                        className={`form-control ${formErrors.birthDate ? 'is-invalid' : 'is-valid'}`}
                        id="regBirthDate"
                        value={form.birthDate}
                        onChange={(event) => handleFieldChange("birthDate", event.target.value)}
                        required
                      />
                      <div className="invalid-feedback">Debes ser mayor de 18 años para registrarte.</div>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label htmlFor="regCountry" className="form-label small fw-bold">
                      Pais
                    </label>
                    <div className="input-group has-validation">
                      <span className="input-group-text">
                        <i className="bi bi-globe" />
                      </span>
                      <select
                        className={`form-select ${formErrors.country ? 'is-invalid' : 'is-valid'}`}
                        id="regCountry"
                        value={form.country}
                        onChange={(event) => handleFieldChange("country", event.target.value)}
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
                      <div className="invalid-feedback">Selecciona un país.</div>
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
                          handleFieldChange("image", event.target.files?.[0])
                        }
                      />
                    </div>
                    <div className="form-text text-muted">Si no subes ninguna, se usara una imagen por defecto.</div>
                  </div>

                  <div className="d-grid mb-4">
                    <button type="submit" className="btn btn-primary py-2 fw-bold" disabled={submitting || !isFormValid}>
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