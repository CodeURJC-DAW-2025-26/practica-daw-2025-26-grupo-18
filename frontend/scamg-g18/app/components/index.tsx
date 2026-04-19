import { useEffect } from "react";
import { useGlobalStore } from "~/stores/globalStore";

export default function AppLayout() {
  const globalData = useGlobalStore().globalData;
  const fetchGlobalData = useGlobalStore().fetchGlobalData;

  // Fetch global para sustituir el mustache con una llamada real a la API
  useEffect(() => {
    void fetchGlobalData();
  }, [fetchGlobalData]);

  const isUserLoggedIn = globalData?.isUserLoggedIn ?? false;
  const isPublisher = globalData?.isPublisher ?? false;

  return (
    <main className="main">

      {/* Hero Section */}
      <section id="hero" className="hero section">

        <div className="container" data-aos="fade-up" data-aos-delay="100">

          <div className="row align-items-center">

            <div className="col-lg-6 order-2 order-lg-1" data-aos="fade-right" data-aos-delay="200">
              <div className="hero-content">
                <h1 className="hero-title">Crea Tu Libertad Financiera con SCAM</h1>
                <p className="hero-description">Aprende a emprender, generar ingresos pasivos y alcanzar la autonomía
                  financiera. Domina las habilidades que te harán independiente y próspero.</p>
                <div className="hero-actions">
                  <a href="/courses" className="btn-primary">Explorar Cursos</a>
                  <a href="https://www.youtube.com/watch?v=fxd3jT3r3BY" className="btn-secondary glightbox">
                    <i className="bi bi-play-circle"></i>
                    <span>Conoce la Academia</span>
                  </a>
                </div>
                <div className="hero-stats">
                  <div className="stat-item">
                    <span className="stat-number">15k+</span>
                    <span className="stat-label">Estudiantes Activos</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-number">$2M+</span>
                    <span className="stat-label">Generados por Alumnos</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-number">24/7</span>
                    <span className="stat-label">Soporte Mentor</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-lg-6 order-1 order-lg-2" data-aos="fade-left" data-aos-delay="300">
              <div className="hero-visual">
                <div className="hero-image-wrapper">
                  <img src="../img/illustration/illustration-15.webp" className="img-fluid hero-image" alt="Hero Image" />
                  <div className="floating-elements">
                    <div className="floating-card card-1">
                      <i className="bi bi-lightbulb"></i>
                      <span>Aprende</span>
                    </div>
                    <div className="floating-card card-2">
                      <i className="bi bi-award"></i>
                      <span>Certifícate</span>
                    </div>
                    <div className="floating-card card-3">
                      <i className="bi bi-people"></i>
                      <span>Comunidad</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

          </div>

        </div>

      </section>{/* /Hero Section */}

      {/* About Section */}
      <section id="about" className="about section">

        <div className="container" data-aos="fade-up" data-aos-delay="100">

          <div className="row gy-5">

            <div className="col-lg-6" data-aos="fade-right" data-aos-delay="200">
              <div className="content-wrapper">
                <div className="section-header">
                  <span className="section-badge">QUIÉNES SOMOS</span>
                  <h2>Tu camino hacia la independencia financiera</h2>
                </div>

                <p className="lead-text">En SCAM creemos que todos podemos lograr libertad financiera a través del
                  emprendimiento y la educación práctica. Sin importar tu situación actual, puedes construir tu propio
                  imperio.</p>

                <p className="description-text">Ofrecemos cursos intensivos en emprendimiento, marketing digital, creación de
                  ingresos pasivos y negocios online. Aprenderás estrategias reales de emprendedores exitosos, cómo validar
                  ideas, escalar negocios y generar riqueza de forma sostenible.</p>

                <div className="stats-grid">
                  <div className="stat-item">
                    <div className="stat-number">45+</div>
                    <div className="stat-label">Cursos Disponibles</div>
                  </div>
                  <div className="stat-item">
                    <div className="stat-number">8.9/10</div>
                    <div className="stat-label">Satisfacción Alumnos</div>
                  </div>
                  <div className="stat-item">
                    <div className="stat-number">300+</div>
                    <div className="stat-label">Emprendedores Activos</div>
                  </div>
                  <div className="stat-item">
                    <div className="stat-number">15+</div>
                    <div className="stat-label">Años en Emprendimiento</div>
                  </div>
                </div>
              </div>
            </div>

            <div className="col-lg-6" data-aos="fade-left" data-aos-delay="300">
              <div className="visual-section">
                <div className="main-image-container">
                  <img src="../img/about/about-8.webp" alt="Estudiantes colaborando" className="img-fluid main-visual" />
                  <div className="overlay-card">
                    <div className="card-content">
                      <h4>Mentores Exitosos</h4>
                      <p>Aprende de emprendedores que han generado millones y construido negocios desde cero.</p>
                      <div className="card-icon">
                        <i className="bi bi-award-fill"></i>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="secondary-images">
                  <div className="row g-3">
                    <div className="col-6">
                      <img src="../img/about/about-11.webp" alt="Team meeting" className="img-fluid secondary-img" />
                    </div>
                    <div className="col-6">
                      <img src="../img/about/about-5.webp" alt="Office workspace" className="img-fluid secondary-img" />
                    </div>
                  </div>
                </div>
              </div>
            </div>

          </div>

          <div className="row mt-5">
            <div className="col-12" data-aos="fade-up" data-aos-delay="400">
              <div className="features-section">
                <div className="row gy-4">
                  <div className="col-md-4">
                    <div className="feature-box">
                      <div className="feature-icon">
                        <i className="bi bi-shield-check"></i>
                      </div>
                      <h5>Validación de Ideas</h5>
                      <p>Aprende a identificar oportunidades reales de mercado y validar tus ideas de negocio.</p>
                    </div>
                  </div>
                  <div className="col-md-4">
                    <div className="feature-box">
                      <div className="feature-icon">
                        <i className="bi bi-lightning-charge"></i>
                      </div>
                      <h5>Lanzamiento Rápido</h5>
                      <p>De idea a primer ingreso en semanas. Metodologías probadas para emprender sin riesgo.
                      </p>
                    </div>
                  </div>
                  <div className="col-md-4">
                    <div className="feature-box">
                      <div className="feature-icon">
                        <i className="bi bi-headset"></i>
                      </div>
                      <h5>Comunidad de Negocios</h5>
                      <p>Conecta con otros emprendedores, colabora en proyectos y expande tu red de contactos.</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>

      </section>{/* /About Section */}

      {/* Features Section */}
      <section id="features" className="features section">

        {/* Section Title */}
        <div className="container section-title" data-aos="fade-up">
          <h2>¿Por qué SCAM es diferente?</h2>
          <p>El lugar donde los emprendedores transforman ideas en negocios rentables</p>
        </div>{/* End Section Title */}

        <div className="container" data-aos="fade-up" data-aos-delay="100">

          <div className="tabs-wrapper">
            <ul className="nav nav-tabs" data-aos="fade-up" data-aos-delay="100">

              <li className="nav-item">
                <a className="nav-link active show" data-bs-toggle="tab" data-bs-target="#features-tab-1">
                  <div className="tab-icon">
                    <i className="bi bi-rocket-takeoff"></i>
                  </div>
                  <div className="tab-content">
                    <h5>Ingresos</h5>
                    <span>Primer $ en 30 días</span>
                  </div>
                </a>
              </li>{/* End tab nav item */}

              <li className="nav-item">
                <a className="nav-link" data-bs-toggle="tab" data-bs-target="#features-tab-2">
                  <div className="tab-icon">
                    <i className="bi bi-shield-shaded"></i>
                  </div>
                  <div className="tab-content">
                    <h5>Escala</h5>
                    <span>De $0 a $10K/mes</span>
                  </div>
                </a>
              </li>{/* End tab nav item */}

              <li className="nav-item">
                <a className="nav-link" data-bs-toggle="tab" data-bs-target="#features-tab-3">
                  <div className="tab-icon">
                    <i className="bi bi-lightning-charge"></i>
                  </div>
                  <div className="tab-content">
                    <h5>Automatización</h5>
                    <span>Ingresos pasivos</span>
                  </div>
                </a>
              </li>{/* End tab nav item */}

              <li className="nav-item">
                <a className="nav-link" data-bs-toggle="tab" data-bs-target="#features-tab-4">
                  <div className="tab-icon">
                    <i className="bi bi-heart-pulse"></i>
                  </div>
                  <div className="tab-content">
                    <h5>Mentoría</h5>
                    <span>Acceso a expertos</span>
                  </div>
                </a>
              </li>{/* End tab nav item */}

            </ul>

            <div className="tab-content" data-aos="fade-up" data-aos-delay="200">

              <div className="tab-pane fade active show" id="features-tab-1">
                <div className="row align-items-center">

                  <div className="col-lg-5">
                    <div className="content-wrapper">
                      <div className="icon-badge">
                        <i className="bi bi-rocket-takeoff"></i>
                      </div>
                      <h3>Tu Primer Ingreso en 30 Días</h3>
                      <p>Enfocamos en resultados reales desde el día uno. Aprenderás estrategias probadas para generar tu
                        primer ingreso mientras aprendes.</p>

                      <div className="feature-grid">
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Acciones inmediatas, no teoría vacía</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Plantillas y herramientas listas para usar</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Acceso de por vida a actualizaciones</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Comunidad de emprendedores activos</span>
                        </div>
                      </div>

                      <div className="stats-row">
                        <div className="stat-item">
                          <div className="stat-number">92%</div>
                          <div className="stat-label">Éxito 1er Mes</div>
                        </div>
                        <div className="stat-item">
                          <div className="stat-number">$3K+</div>
                          <div className="stat-label">Ingreso Promedio</div>
                        </div>
                        <div className="stat-item">
                          <div className="stat-number">24/7</div>
                          <div className="stat-label">Soporte</div>
                        </div>
                      </div>

                      <a href="#" className="btn-primary">Empezar Ahora <i className="bi bi-arrow-right"></i></a>
                    </div>
                  </div>

                  <div className="col-lg-7">
                    <div className="visual-content">
                      <div className="main-image">
                        <img src="../img/features/features-4.webp" alt="" className="img-fluid" />
                        <div className="floating-card">
                          <i className="bi bi-graph-up-arrow"></i>
                          <div className="card-content">
                            <span>Performance</span>
                            <strong>+85% Improvement</strong>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                </div>
              </div>{/* End tab content item */}

              <div className="tab-pane fade" id="features-tab-2">
                <div className="row align-items-center">

                  <div className="col-lg-5">
                    <div className="content-wrapper">
                      <div className="icon-badge">
                        <i className="bi bi-shield-shaded"></i>
                      </div>
                      <h3>Escala de $0 a $10K/mes</h3>
                      <p>Aprende el framework que miles de emprendedores usan para pasar de idea a negocio rentable en
                        meses.</p>

                      <div className="feature-grid">
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Estrategias de crecimiento exponencial</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Técnicas de venta que funcionan online</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Casos de éxito reales de nuestros alumnos</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Roadmap personalizado para tu negocio</span>
                        </div>
                      </div>

                      <div className="stats-row">
                        <div className="stat-item">
                          <div className="stat-number">$8.5K</div>
                          <div className="stat-label">Ingreso Promedio</div>
                        </div>
                        <div className="stat-item">
                          <div className="stat-number">6</div>
                          <div className="stat-label">Meses a $10K</div>
                        </div>
                        <div className="stat-item">
                          <div className="stat-number">87%</div>
                          <div className="stat-label">Llegan Meta</div>
                        </div>
                      </div>

                      <a href="#" className="btn-primary">Ver Más <i className="bi bi-arrow-right"></i></a>
                    </div>
                  </div>

                  <div className="col-lg-7">
                    <div className="visual-content">
                      <div className="main-image">
                        <img src="../img/features/features-2.webp" alt="" className="img-fluid" />
                        <div className="floating-card">
                          <i className="bi bi-shield-check"></i>
                          <div className="card-content">
                            <span>Security</span>
                            <strong>Military Grade</strong>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                </div>
              </div>{/* End tab content item */}

              <div className="tab-pane fade" id="features-tab-3">
                <div className="row align-items-center">

                  <div className="col-lg-5">
                    <div className="content-wrapper">
                      <div className="icon-badge">
                        <i className="bi bi-lightning-charge"></i>
                      </div>
                      <h3>Ingresos Pasivos Reales</h3>
                      <p>Construye sistemas que generan dinero mientras duermes. Automatiza, documenta y replica lo que
                        funciona.</p>

                      <div className="feature-grid">
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Modelos de negocio de bajo mantenimiento</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Automatización con herramientas gratuitas</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Productización y escalabilidad</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Diversificación de ingresos</span>
                        </div>
                      </div>

                      <div className="stats-row">
                        <div className="stat-item">
                          <div className="stat-number">$2K-5K</div>
                          <div className="stat-label">Ingresos Pasivos</div>
                        </div>
                        <div className="stat-item">
                          <div className="stat-number">8-10</div>
                          <div className="stat-label">Horas/Semana</div>
                        </div>
                        <div className="stat-item">
                          <div className="stat-number">3+</div>
                          <div className="stat-label">Fuentes Ingreso</div>
                        </div>
                      </div>

                      <a href="#" className="btn-primary">Descubre Más <i className="bi bi-arrow-right"></i></a>
                    </div>
                  </div>

                  <div className="col-lg-7">
                    <div className="visual-content">
                      <div className="main-image">
                        <img src="../img/features/features-6.webp" alt="" className="img-fluid" />
                        <div className="floating-card">
                          <i className="bi bi-speedometer2"></i>
                          <div className="card-content">
                            <span>Speed</span>
                            <strong>Ultra Fast</strong>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                </div>
              </div>{/* End tab content item */}

              <div className="tab-pane fade" id="features-tab-4">
                <div className="row align-items-center">

                  <div className="col-lg-5">
                    <div className="content-wrapper">
                      <div className="icon-badge">
                        <i className="bi bi-heart-pulse"></i>
                      </div>
                      <h3>Mentoría y Apoyo Personalizado</h3>
                      <p>No estás solo en este viaje. Acceso a mentores exitosos, comunidad y soporte directo para tus
                        desafíos.</p>

                      <div className="feature-grid">
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Sesiones de mentoría uno a uno</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Comunidad privada de emprendedores</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Acceso a expertos en tu nicho</span>
                        </div>
                        <div className="feature-item">
                          <i className="bi bi-check-circle-fill"></i>
                          <span>Resolución de problemas en tiempo real</span>
                        </div>
                      </div>

                      <div className="stats-row">
                        <div className="stat-item">
                          <div className="stat-number">24/7</div>
                          <div className="stat-label">Disponible</div>
                        </div>
                        <div className="stat-item">
                          <div className="stat-number">&lt;1h</div>
                          <div className="stat-label">Respuesta</div>
                        </div>
                        <div className="stat-item">
                          <div className="stat-number">100+</div>
                          <div className="stat-label">Mentores</div>
                        </div>
                      </div>

                      <a href="#" className="btn-primary">Únete Ahora <i className="bi bi-arrow-right"></i></a>
                    </div>
                  </div>

                  <div className="col-lg-7">
                    <div className="visual-content">
                      <div className="main-image">
                        <img src="../img/features/features-1.webp" alt="" className="img-fluid" />
                        <div className="floating-card">
                          <i className="bi bi-headset"></i>
                          <div className="card-content">
                            <span>Comunidad</span>
                            <strong>Siempre ahí</strong>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                </div>
              </div>{/* End tab content item */}

            </div>
          </div>

        </div>

      </section>{/* /Features Section */}

      {/* Call To Action Section */}
      <section id="call-to-action" className="call-to-action section dark-background">

        <div className="container" data-aos="fade-up" data-aos-delay="100">

          <div className="row align-items-lg-center">
            <div className="col-lg-5 order-lg-2" data-aos="fade-left" data-aos-delay="200">
              <div className="image-wrapper position-relative">
                <div className="floating-card">
                  <i className="bi bi-lightning-fill"></i>
                  <h4>Libertad Financiera</h4>
                  <p>Cada día más cerca de tomar decisiones basadas en deseos, no en necesidades.</p>
                </div>
                <img src="../img/misc/misc-6.webp" alt="Libertad Financiera" className="img-fluid main-image" />
              </div>
            </div>

            <div className="col-lg-6 offset-lg-1 order-lg-1" data-aos="fade-right" data-aos-delay="100">
              <div className="content-area">
                <h2>De Empleado a Emprendedor en 90 Días</h2>
                <p>Miles de personas han transformado sus vidas pasando de vivir quincena a quincena a generar ingresos
                  múltiples y alcanzar la libertad financiera. Tú puedes ser el siguiente.</p>

                <ul className="feature-list">
                  <li>
                    <i className="bi bi-check"></i>
                    <span>Genera tu primer ingreso en 30 días o menos</span>
                  </li>
                  <li>
                    <i className="bi bi-check"></i>
                    <span>Crea sistemas que no requieren tu presencia 24/7</span>
                  </li>
                  <li>
                    <i className="bi bi-check"></i>
                    <span>Multiplica tus ingresos y construye riqueza real</span>
                  </li>
                </ul>

                <div className="cta-wrapper">
                  <a href="#" className="btn btn-cta">Inicia Tu Transformación</a>
                </div>
              </div>
            </div>

          </div>

        </div>

      </section>{/* /Call To Action Section */}

      {/* Testimonials Section */}
      <section id="testimonials" className="testimonials section">

        {/* Section Title */}
        <div className="container section-title" data-aos="fade-up">
          <span className="description-title">Historias de Éxito</span>
          <h2>Nuestros Emprendedores Hablan</h2>
          <p>Resultados reales de personas que decidieron cambiar sus vidas</p>
        </div>{/* End Section Title */}

        <div className="container">

          <div className="testimonial-masonry">

            <div className="testimonial-item" data-aos="fade-up">
              <div className="testimonial-content">
                <div className="quote-pattern">
                  <i className="bi bi-quote"></i>
                </div>
                <p>Pasé de ganar $2000/mes a $8000/mes en 4 meses. Ahora tengo dos negocios online que me generan ingresos
                  pasivos. ¡Finalmente respiro tranquila!.</p>
                <div className="client-info">
                  <div className="client-image">
                    <img src="../img/person/person-f-7.webp" alt="Client" />
                  </div>
                  <div className="client-details">
                    <h3>Rachel Bennett</h3>
                    <span className="position">Emprendedora Digital</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="testimonial-item highlight" data-aos="fade-up" data-aos-delay="100">
              <div className="testimonial-content">
                <div className="quote-pattern">
                  <i className="bi bi-quote"></i>
                </div>
                <p>Dejé mi empleo después de 6 meses en SCAM. Hoy mi negocio genera $15K/mes y tengo libertad total para
                  vivir donde quiero. Este programa cambió mi perspectiva completamente.</p>
                <div className="client-info">
                  <div className="client-image">
                    <img src="../img/person/person-m-7.webp" alt="Client" />
                  </div>
                  <div className="client-details">
                    <h3>Daniel Morgan</h3>
                    <span className="position">Emprendedor Independiente</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="testimonial-item" data-aos="fade-up" data-aos-delay="200">
              <div className="testimonial-content">
                <div className="quote-pattern">
                  <i className="bi bi-quote"></i>
                </div>
                <p>Creé un negocio de consultoría digital y en 5 meses alcancé mis primeros $20K. Los sistemas de SCAM
                  funcionan y son adaptables a cualquier nicho.</p>
                <div className="client-info">
                  <div className="client-image">
                    <img src="../img/person/person-f-8.webp" alt="Client" />
                  </div>
                  <div className="client-details">
                    <h3>Emma Thompson</h3>
                    <span className="position">Consultora Digital</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="testimonial-item" data-aos="fade-up" data-aos-delay="300">
              <div className="testimonial-content">
                <div className="quote-pattern">
                  <i className="bi bi-quote"></i>
                </div>
                <p>Nunca pensé que podría emprender. Ahora tengo 3 fuentes de ingresos pasivos y trabajo solo 10 horas a la
                  semana. SCAM me enseñó el roadmap exacto.</p>
                <div className="client-info">
                  <div className="client-image">
                    <img src="../img/person/person-m-8.webp" alt="Client" />
                  </div>
                  <div className="client-details">
                    <h3>Christopher Lee</h3>
                    <span className="position">Emprendedor de Tiempo Parcial</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="testimonial-item highlight" data-aos="fade-up" data-aos-delay="400">
              <div className="testimonial-content">
                <div className="quote-pattern">
                  <i className="bi bi-quote"></i>
                </div>
                <p>Lo que más me gustó fue lo práctico. No es teoría, es lo que funciona AHORA en el mercado. Generé $5K en
                  mi primer mes y sigo creciendo. Increíble.</p>
                <div className="client-info">
                  <div className="client-image">
                    <img src="../img/person/person-f-9.webp" alt="Client" />
                  </div>
                  <div className="client-details">
                    <h3>Olivia Carter</h3>
                    <span className="position">Creadora de Contenido</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="testimonial-item" data-aos="fade-up" data-aos-delay="500">
              <div className="testimonial-content">
                <div className="quote-pattern">
                  <i className="bi bi-quote"></i>
                </div>
                <p>Hace un año estaba endeudado. Ahora tengo 2 negocios generando ingresos pasivos mensuales. La mentoría de
                  SCAM fue el catalizador que necesitaba.</p>
                <div className="client-info">
                  <div className="client-image">
                    <img src="../img/person/person-m-13.webp" alt="Client" />
                  </div>
                  <div className="client-details">
                    <h3>Nathan Brooks</h3>
                    <span className="position">Empresario Fintech</span>
                  </div>
                </div>
              </div>
            </div>

          </div>

        </div>

      </section>{/* /Testimonials Section */}

      {/* Services Section */}
      <section id="services" className="services section">

        {/* Section Title */}
        <div className="container section-title" data-aos="fade-up">
          <span className="description-title">Nuestros Cursos</span>
          <h2>Especialidades para Emprendedores</h2>
          <p>Elige tu camino hacia la libertad financiera</p>
        </div>{/* End Section Title */}

        <div className="container" data-aos="fade-up" data-aos-delay="100">

          <div className="row align-items-center">
            <div className="col-lg-6">
              <div className="intro-content" data-aos="fade-right" data-aos-delay="100">
                <div className="section-badge mb-3" data-aos="zoom-in" data-aos-delay="50">
                  <i className="bi bi-star-fill"></i>
                  <span>LO MÁS POPULAR</span>
                </div>
                <h2 className="section-heading mb-4">Cursos que Generan Ingresos</h2>
                <p className="section-description mb-4">Cada curso está diseñado para que generes dinero desde el primer mes.
                  Usa las mismas estrategias que empleados exitosos usan para construir imperios.</p>
                <a href="#" className="cta-button" data-aos="fade-right" data-aos-delay="200">
                  Ver todos los cursos
                </a>
              </div>
            </div>
            <div className="col-lg-6">
              <div className="hero-visual" data-aos="fade-left" data-aos-delay="150">
                <img src="../img/services/services-1.webp" alt="Cursos" className="img-fluid" />
              </div>
            </div>
          </div>
        </div>

      </section>{/* /Services Section */}

      {/* Pricing Section */}
      <section id="pricing" className="pricing section">

        {/* Section Title */}
        <div className="container section-title" data-aos="fade-up">
          <span className="description-title">Planes</span>
          <h2>Elige tu Suscripción</h2>
          <p>Plan gratuito por defecto al iniciar sesión y plan premium para crear cursos y eventos.</p>
        </div>{/* End Section Title */}

        <div className="container" data-aos="fade-up" data-aos-delay="100">

          <div className="row gy-4 justify-content-center">

            <div className="col-lg-5" data-aos="fade-up" data-aos-delay="200">
              <article className="price-card pricing-item h-100">
                <div className="card-head">
                  <span className="badge-title">Gratis</span>
                  <h3 className="title">Plan Básico</h3>
                  <p className="subtitle">Se activa por defecto al iniciar sesión.</p>
                  <div className="price-wrap">
                    <span className="price"><sup>€</sup>0<span className="period">/mes</span></span>
                  </div>
                </div>

                <ul className="feature-list list-unstyled mb-4">
                  <li><i className="bi bi-check-circle"></i> Acceso a la plataforma</li>
                  <li><i className="bi bi-check-circle"></i> Ver cursos y eventos</li>
                  <li><i className="bi bi-check-circle"></i> Comprar cursos y eventos</li>
                  <li className="muted"><i className="bi bi-dash-circle"></i> No permite crear cursos ni eventos</li>
                </ul>

                <div className="cta">
                  {isUserLoggedIn ? (
                    isPublisher ? (
                      <span className="btn btn-choose w-100 disabled">Incluido en Premium</span>
                    ) : (
                      <span className="btn btn-choose w-100 disabled">Plan actual</span>
                    )
                  ) : (
                    <a href="/login" className="btn btn-choose w-100">Iniciar sesión</a>
                  )}
                </div>
              </article>
            </div>

            <div className="col-lg-5" data-aos="fade-up" data-aos-delay="250">
              <article className="price-card featured pricing-item h-100 position-relative">
                <div className="ribbon"><i className="bi bi-star-fill"></i> Premium</div>

                <div className="card-head">
                  <span className="badge-title">Premium</span>
                  <h3 className="title">Creador</h3>
                  <p className="subtitle">Permite crear cursos y eventos.</p>
                  <div className="price-wrap">
                    <span className="price"><sup>€</sup>10<span className="period">/mes</span></span>
                  </div>
                </div>

                <ul className="feature-list list-unstyled mb-4">
                  <li><i className="bi bi-check-circle"></i> Todo lo del plan gratis</li>
                  <li><i className="bi bi-check-circle"></i> Crear cursos</li>
                  <li><i className="bi bi-check-circle"></i> Crear eventos</li>
                  <li><i className="bi bi-check-circle"></i> Rol de creador tras la compra</li>
                </ul>

                <div className="cta">
                  {isUserLoggedIn ? (
                    isPublisher ? (
                      <span className="btn btn-choose w-100 disabled">Ya tienes Premium</span>
                    ) : (
                      <form action="/cart/add/subscription" method="post">
                        <button type="submit" className="btn btn-choose w-100">Comprar por 10€</button>
                      </form>
                    )
                  ) : (
                    <a href="/login" className="btn btn-choose w-100">Iniciar sesión para comprar</a>
                  )}
                </div>
              </article>
            </div>

          </div>

        </div>

      </section>{/* /Pricing Section */}

      {/* Faq Section */}
      <section id="faq" className="faq section">

        <div className="container">

          <div className="row justify-content-center">

            <div className="col-lg-10" data-aos="fade-up" data-aos-delay="100">

              <div className="faq-container">

                <div className="faq-item faq-active">
                  <h3>¿En cuánto tiempo puedo generar mi primer ingreso?</h3>
                  <div className="faq-content">
                    <p>El 92% de nuestros alumnos generan su primer ingreso dentro de los primeros 30 días. El tiempo exacto
                      depende de tu dedicación y la aplicación de las estrategias, pero tenemos casos de personas que
                      generaron dinero en la primera semana.</p>
                  </div>
                  <i className="faq-toggle bi bi-chevron-right"></i>
                </div>{/* End Faq item */}

                <div className="faq-item">
                  <h3>¿Necesito experiencia previa para empezar?</h3>
                  <div className="faq-content">
                    <p>No necesitas experiencia previa. Nuestros cursos están diseñados desde cero. Si puedes enviar un
                      correo electrónico, puedes usar nuestras estrategias. Hemos trabajado con estudiantes de secundaria
                      hasta jubilados, y todos han generado ingresos.</p>
                  </div>
                  <i className="faq-toggle bi bi-chevron-right"></i>
                </div>{/* End Faq item */}

                <div className="faq-item">
                  <h3>¿Cuánto dinero puedo realmente ganar?</h3>
                  <div className="faq-content">
                    <p>Depende de ti. Nuestro promedio es $3,000/mes en el primer mes, con alcance a $10,000+ en 6 meses.
                      Algunos alumnos llegan a $20K+ mensualmente. No hay límite, solo tu dedicación y ejecución.</p>
                  </div>
                  <i className="faq-toggle bi bi-chevron-right"></i>
                </div>{/* End Faq item */}

                <div className="faq-item">
                  <h3>¿Qué sucede si no logro generar ingresos?</h3>
                  <div className="faq-content">
                    <p>Ofrecemos garantía de satisfacción. Si completas todos los módulos, sigues el plan día a día y aún
                      así no ves resultados en 60 días, te devolvemos tu dinero 100%. Pero honestamente, si aplicas lo que
                      enseñamos, funcionará.</p>
                  </div>
                  <i className="faq-toggle bi bi-chevron-right"></i>
                </div>{/* End Faq item */}

                <div className="faq-item">
                  <h3>¿Puedo tomar los cursos a mi propio ritmo?</h3>
                  <div className="faq-content">
                    <p>Sí, tienes acceso de por vida al contenido. Puedes estudiar a tu ritmo, repasar lo que necesites, y
                      acceder desde cualquier dispositivo. Es flexible porque entendemos que todos tenemos diferentes
                      horarios.</p>
                  </div>
                  <i className="faq-toggle bi bi-chevron-right"></i>
                </div>{/* End Faq item */}

                <div className="faq-item">
                  <h3>¿Qué tipos de negocios puedo crear?</h3>
                  <div className="faq-content">
                    <p>Las estrategias de SCAM funcionan para cualquier nicho: servicios freelance, productos digitales,
                      marketing de afiliados, coaching, consultoría, dropshipping, etc. Lo importante es que escojas algo
                      que te apasione y nosotros te enseñamos el camino probado.</p>
                  </div>
                  <i className="faq-toggle bi bi-chevron-right"></i>
                </div>{/* End Faq item */}

              </div>

            </div>{/* End Faq Column */}

          </div>

        </div>

      </section>{/* /Faq Section */}

    </main>
  );
}
