import { Link, useSearchParams } from "react-router";
import { Container } from "react-bootstrap";

export default function ErrorPage() {
  const [searchParams] = useSearchParams();
  const message = searchParams.get("message") || "Ha ocurrido un error inesperado. Por favor, inténtalo de nuevo más tarde.";

  return (
    <main className="main min-vh-100 d-flex flex-column">
      <div className="page-title light-background mb-4">
        <Container>
          <nav className="breadcrumbs mb-2">
            <ol className="list-unstyled d-flex gap-2 m-0 p-0 small">
              <li><Link to="/new">Inicio</Link></li>
              <li className="current text-muted">Error</li>
            </ol>
          </nav>
          <h1 className="m-0 h2 fw-bold text-danger">Aviso Importante</h1>
        </Container>
      </div>

      <section className="section py-5 flex-grow-1 d-flex align-items-center">
        <Container className="text-center">
          <div className="alert alert-warning border-0 shadow-sm mx-auto p-5" style={{ maxWidth: "600px", borderRadius: "16px" }}>
            <i className="bi bi-exclamation-triangle fs-1 d-block mb-3 text-warning"></i>
            <h4 className="alert-heading fw-bold mb-3">Acceso no permitido</h4>
            <p className="mb-0 fs-5 text-dark">{message}</p>
          </div>
          <div className="mt-4">
            <Link to="/new" className="btn btn-accent px-4 py-2 rounded-pill fw-medium shadow-sm">
              <i className="bi bi-house-door me-2"></i>Volver al Inicio
            </Link>
          </div>
        </Container>
      </section>
    </main>
  );
}
