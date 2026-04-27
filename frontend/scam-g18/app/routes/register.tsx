import type { FormEvent } from "react";
import { useState } from "react";
import { Link, redirect, useNavigate } from "react-router";
import { Alert, Button, Col, Container, Form, InputGroup, Row } from "react-bootstrap";

import { register as registerRequest } from "~/services/authService";
import { loadGlobalDataIntoStore } from "~/services/globalService";
import { publicAsset } from "~/utils/publicAsset";

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

const SORTED_COUNTRIES = [...COUNTRIES].sort((a, b) => a.localeCompare(b, "es"));

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
  // Save touched fields to show it after validation, if required
  const [touched, setTouched] = useState<Record<string, boolean>>({});

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
        <Container>
          <Row className="g-0 shadow-lg rounded-4 overflow-hidden" style={{ minHeight: 600 }}>
            <Col lg={6} className="d-none d-lg-block position-relative">
              <img src={publicAsset("services/Services-3.webp")} alt="Join SCAM" className="img-fluid w-100 h-100" style={{ objectFit: "cover", objectPosition: "center" }} />
              <div className="position-absolute top-0 start-0 w-100 h-100" style={{ background: "rgba(0, 0, 0, 0.3)" }} />
              <div className="position-absolute bottom-0 start-0 p-5 text-white">
                <h3 className="fw-bold text-white">Unete a nuestra comunidad</h3>
                <p className="lead mb-0">Aprende de los mejores expertos y transforma tu carrera profesional hoy mismo.</p>
              </div>
            </Col>

            <Col lg={6} className="bg-white d-flex align-items-center">
              <div className="p-5 w-100">
                <div className="text-center mb-4">
                  <h2 className="fw-bold text-dark">Crear Cuenta</h2>
                  <p className="text-muted">Empieza tu aprendizaje hoy</p>
                </div>

                {error && (
                  <Alert variant="danger" role="alert">
                    {error}
                  </Alert>
                )}

                <Form id="registerForm" onSubmit={onSubmit} encType="multipart/form-data" noValidate>
                  <Form.Group className="mb-3" controlId="regUsername">
                    <Form.Label className="small fw-bold">
                      Nombre de usuario
                    </Form.Label>
                    <InputGroup hasValidation>
                      <InputGroup.Text>
                        <i className="bi bi-person" />
                      </InputGroup.Text>
                      <Form.Control
                        type="text"
                        value={form.username}
                        onChange={(event) => handleFieldChange("username", event.target.value)}
                        placeholder="Tu nombre de usuario"
                        isInvalid={!!touched.username && formErrors.username}
                        isValid={!!touched.username && !formErrors.username}
                        required
                      />
                      <Form.Control.Feedback type="invalid">El usuario debe tener entre 3 y 20 caracteres.</Form.Control.Feedback>
                    </InputGroup>
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="regEmail">
                    <Form.Label className="small fw-bold">
                      Correo electronico
                    </Form.Label>
                    <InputGroup hasValidation>
                      <InputGroup.Text>
                        <i className="bi bi-envelope" />
                      </InputGroup.Text>
                      <Form.Control
                        type="email"
                        value={form.email}
                        onChange={(event) => handleFieldChange("email", event.target.value)}
                        placeholder="nombre@ejemplo.com"
                        isInvalid={!!touched.email && formErrors.email}
                        isValid={!!touched.email && !formErrors.email}
                        required
                      />
                      <Form.Control.Feedback type="invalid">Introduce un correo electrónico válido.</Form.Control.Feedback>
                    </InputGroup>
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="regPassword">
                    <Form.Label className="small fw-bold">
                      Contrasena
                    </Form.Label>
                    <InputGroup hasValidation>
                      <InputGroup.Text>
                        <i className="bi bi-lock" />
                      </InputGroup.Text>
                      <Form.Control
                        type="password"
                        value={form.password}
                        onChange={(event) => handleFieldChange("password", event.target.value)}
                        placeholder="********"
                        isInvalid={!!touched.password && formErrors.password}
                        isValid={!!touched.password && !formErrors.password}
                        required
                      />
                      <Form.Control.Feedback type="invalid">Min. 8 caracteres: 1 mayuscula, 1 numero y 7 letras.</Form.Control.Feedback>
                    </InputGroup>
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="regGender">
                    <Form.Label className="small fw-bold">
                      Genero
                    </Form.Label>
                    <InputGroup hasValidation>
                      <InputGroup.Text>
                        <i className="bi bi-gender-ambiguous" />
                      </InputGroup.Text>
                      <Form.Select
                        value={form.gender}
                        onChange={(event) => handleFieldChange("gender", event.target.value)}
                        isInvalid={!!touched.gender && formErrors.gender}
                        isValid={!!touched.gender && !formErrors.gender}
                        required
                      >
                        <option value="" disabled>
                          Selecciona una opcion
                        </option>
                        <option value="MALE">Masculino</option>
                        <option value="FEMALE">Femenino</option>
                        <option value="PREFER_NOT_TO_SAY">Prefiero no decirlo</option>
                      </Form.Select>
                      <Form.Control.Feedback type="invalid">Selecciona un genero.</Form.Control.Feedback>
                    </InputGroup>
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="regBirthDate">
                    <Form.Label className="small fw-bold">
                      Fecha de nacimiento
                    </Form.Label>
                    <InputGroup hasValidation>
                      <InputGroup.Text>
                        <i className="bi bi-calendar" />
                      </InputGroup.Text>
                      <Form.Control
                        type="date"
                        value={form.birthDate}
                        onChange={(event) => handleFieldChange("birthDate", event.target.value)}
                        isInvalid={!!touched.birthDate && formErrors.birthDate}
                        isValid={!!touched.birthDate && !formErrors.birthDate}
                        required
                      />
                      <Form.Control.Feedback type="invalid">Debes ser mayor de 18 anos para registrarte.</Form.Control.Feedback>
                    </InputGroup>
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="regCountry">
                    <Form.Label className="small fw-bold">
                      Pais
                    </Form.Label>
                    <InputGroup hasValidation>
                      <InputGroup.Text>
                        <i className="bi bi-globe" />
                      </InputGroup.Text>
                      <Form.Select
                        value={form.country}
                        onChange={(event) => handleFieldChange("country", event.target.value)}
                        isInvalid={!!touched.country && formErrors.country}
                        isValid={!!touched.country && !formErrors.country}
                        required
                      >
                        <option value="" disabled>
                          Selecciona tu pais
                        </option>
                        {SORTED_COUNTRIES.map((country) => (
                          <option key={country} value={country}>
                            {country}
                          </option>
                        ))}
                      </Form.Select>
                      <Form.Control.Feedback type="invalid">Selecciona un pais.</Form.Control.Feedback>
                    </InputGroup>
                  </Form.Group>

                  <Form.Group className="mb-4" controlId="regImage">
                    <Form.Label className="small fw-bold">
                      Foto de perfil <span className="text-muted fw-normal">(opcional)</span>
                    </Form.Label>
                    <InputGroup>
                      <InputGroup.Text>
                        <i className="bi bi-image" />
                      </InputGroup.Text>
                      <Form.Control
                        type="file"
                        accept="image/*"
                        onChange={(event) => handleFieldChange("image", (event.currentTarget as HTMLInputElement).files?.[0])}
                      />
                    </InputGroup>
                    <Form.Text className="text-muted">Si no subes ninguna, se usara una imagen por defecto.</Form.Text>
                  </Form.Group>

                  <div className="d-grid mb-4">
                    <Button type="submit" variant="primary" className="py-2 fw-bold" disabled={submitting || !isFormValid}>
                      {submitting ? "Registrando..." : "Registrarse"}
                    </Button>
                  </div>
                </Form>

                <div className="text-center p-3 rounded bg-light">
                  <p className="mb-0 small">
                    Ya tienes cuenta?{" "}
                    <Link to="/new/login" className="fw-bold text-decoration-none" style={{ color: "var(--accent-color)" }}>
                      Iniciar Sesion
                    </Link>
                  </p>
                </div>
              </div>
            </Col>
          </Row>
        </Container>
      </section>
    </main>
  );
}