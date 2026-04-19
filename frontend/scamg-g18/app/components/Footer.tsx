import { Container, Row, Col } from "react-bootstrap";

export default function Footer() {
    return (
        <footer id="footer" className="footer position-relative dark-background py-5 mt-auto">
            <Container className="footer-top">
                <Row className="g-4 footer-main-row">
                    <Col xs={12} className="footer-intro text-center">
                        <a href="/" className="logo d-inline-flex align-items-center justify-content-center text-decoration-none">
                            <span className="sitename fs-4 fw-bold">SCAM</span>
                        </a>
                        <p className="footer-lead mb-0 mt-2">
                            En SCAM ayudamos a estudiantes y emprendedores a transformar ideas en proyectos reales con cursos practicos, eventos especializados y una comunidad que impulsa el crecimiento profesional paso a paso.
                        </p>
                    </Col>

                    <Col xs={12}>
                        <Row className="g-3 footer-info-grid">
                            <Col lg={4} md={6}>
                                <article className="footer-info-card">
                                    <h4>Direccion</h4>
                                    <p>Calle Tulipan, s/n</p>
                                    <p>Mostoles (Madrid) - 28933</p>
                                    <p className="mb-0">Espana</p>
                                </article>
                            </Col>

                            <Col lg={3} md={6}>
                                <article className="footer-info-card">
                                    <h4>Telefono</h4>
                                    <p className="mb-0">
                                        <a href="tel:+34911234567">+34 91 123 45 67</a>
                                    </p>
                                </article>
                            </Col>

                            <Col lg={5} md={12}>
                                <article className="footer-info-card">
                                    <h4>Emails</h4>
                                    <ul className="footer-email-list">
                                        <li>
                                            <a href="mailto:p.calvo.2023@alumnos.urjc.es">p.calvo.2023@alumnos.urjc.es</a>
                                        </li>
                                        <li>
                                            <a href="mailto:a.hontanilla.2023@alumnos.urjc.es">a.hontanilla.2023@alumnos.urjc.es</a>
                                        </li>
                                        <li>
                                            <a href="mailto:ga.zurdo.2023@alumnos.urjc.es">ga.zurdo.2023@alumnos.urjc.es</a>
                                        </li>
                                        <li>
                                            <a href="mailto:j.sanchezva.2023@alumnos.urjc.es">j.sanchezva.2023@alumnos.urjc.es</a>
                                        </li>
                                    </ul>
                                </article>
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </Container>

            <Container className="copyright text-center mt-4">
                <p>
                    © <span>Copyright</span> <strong className="px-1 sitename">SCAM</strong> <span>All Rights Reserved</span>
                </p>
                <div className="credits">
                    Designed by <a href="/">Grupo 18</a> | <a href="/">SCAM</a>
                </div>
            </Container>
        </footer>
    );
}
