import { Navbar, Nav, Container } from 'react-bootstrap';
import { Link, NavLink } from 'react-router';

export default function Header() {
  return (
    <Navbar bg="dark" variant="dark" expand="lg">
      <Container>
        <Navbar.Brand as={Link} to="/">SCAM G-18</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={NavLink} to="/">Inicio</Nav.Link>
            <Nav.Link as={NavLink} to="/courses">Cursos</Nav.Link>
            <Nav.Link as={NavLink} to="/events">Eventos</Nav.Link>
          </Nav>
          <Nav>
            <Nav.Link as={NavLink} to="/login">Iniciar Sesión</Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
