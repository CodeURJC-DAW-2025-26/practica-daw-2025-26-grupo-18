import type { FormEvent } from "react";
import { useState } from "react";
import { Alert, Button, Col, Container, Form, InputGroup, Row } from "react-bootstrap";
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

      navigate("/new", { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error al iniciar sesión.");
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
              <img src="/services/Services-3.webp" alt="Join SCAM" className="img-fluid w-100 h-100" style={{ objectFit: "cover", objectPosition: "center" }} />
              <div className="position-absolute top-0 start-0 w-100 h-100" style={{ background: "rgba(0, 0, 0, 0.3)" }} />
              <div className="position-absolute bottom-0 start-0 p-5 text-white">
                <h3 className="fw-bold text-white">Unete a nuestra comunidad</h3>
                <p className="lead mb-0">Aprende de los mejores expertos y transforma tu carrera profesional hoy mismo.</p>
              </div>
            </Col>

            <Col lg={6} className="bg-white d-flex align-items-center">
              <div className="p-5 w-100">
                <div className="text-center mb-4">
                  <h2 className="fw-bold text-dark">Iniciar Sesion</h2>
                  <p className="text-muted">Bienvenido de nuevo a SCAM</p>
                </div>

                {error && (
                  <Alert variant="danger" role="alert">
                    {error}
                  </Alert>
                )}

                <Form id="loginForm" onSubmit={onSubmit}>
                  <Form.Group className="mb-3" controlId="loginEmail">
                    <Form.Label className="small fw-bold">
                      Usuario o Correo electronico
                    </Form.Label>
                    <InputGroup>
                      <InputGroup.Text>
                        <i className="bi bi-person" />
                      </InputGroup.Text>
                      <Form.Control
                        type="text"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        placeholder="Usuario o correo electronico"
                        autoComplete="username"
                      />
                    </InputGroup>
                  </Form.Group>

                  <Form.Group className="mb-4" controlId="loginPassword">
                    <Form.Label className="small fw-bold">
                      Contrasena
                    </Form.Label>
                    <InputGroup>
                      <InputGroup.Text>
                        <i className="bi bi-lock" />
                      </InputGroup.Text>
                      <Form.Control
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        placeholder="********"
                        autoComplete="current-password"
                      />
                    </InputGroup>
                  </Form.Group>

                  <div className="d-grid mb-3">
                    <Button type="submit" className="py-2" style={{ backgroundColor: "var(--accent-color)", borderColor: "var(--accent-color)" }} disabled={submitting}>
                      {submitting ? "Iniciando..." : "Iniciar Sesion"}
                    </Button>
                  </div>
                </Form>

                <div className="text-center p-3 rounded bg-light">
                  <p className="mb-0 small">
                    No tienes cuenta?{" "}
                    <Link to="/new/register" className="fw-bold text-decoration-none" style={{ color: "var(--accent-color)" }}>
                      Registrarse
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