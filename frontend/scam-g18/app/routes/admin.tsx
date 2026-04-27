import { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams, redirect } from "react-router";

import {
  getAdminCourses,
  getAdminEvents,
  getAdminOrders,
  getAdminUsers,
  updateAdminCourseStatus,
  updateAdminEventStatus,
  updateAdminUserStatus,
} from "~/services/adminService";
import type {
  AdminCourseDTO,
  AdminEventDTO,
  AdminOrderDTO,
  AdminUserDTO,
} from "~/dtos/AdminDTO";
import { useGlobalStore } from "~/stores/globalStore";
import { loadGlobalDataIntoStore } from "~/services/globalService";

export async function clientLoader() {
  const globalData = await loadGlobalDataIntoStore();
  if (!globalData?.isAdmin) {
    // No admin privileges, redirect to error page with message
    throw redirect(`/new/error?message=${encodeURIComponent("No tienes permisos para acceder al dashboard de administrador.")}`);
  }
  return null;
}

clientLoader.hydrate = true;

type TabKey = "users" | "courses" | "events" | "orders";

const PAGE_SIZE = 10;

function formatDate(value?: string): string {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("es-ES");
}

export default function AdminRoute() {
  const [searchParams, setSearchParams] = useSearchParams();
  const tabParam = searchParams.get("tab");
  const activeTab: TabKey =
    tabParam === "users" || tabParam === "courses" || tabParam === "events" || tabParam === "orders" ? tabParam : "users";

  const { globalData, startRequest, endRequest } = useGlobalStore();

  const [error, setError] = useState<string | null>(null);

  const [users, setUsers] = useState<AdminUserDTO[]>([]);
  const [userPage, setUserPage] = useState(0);
  const [userQuery, setUserQuery] = useState("");
  const [usersLoading, setUsersLoading] = useState(false);
  const [usersHasMore, setUsersHasMore] = useState(true);

  const [courses, setCourses] = useState<AdminCourseDTO[]>([]);
  const [coursePage, setCoursePage] = useState(0);
  const [courseQuery, setCourseQuery] = useState("");
  const [coursesLoading, setCoursesLoading] = useState(false);
  const [coursesHasMore, setCoursesHasMore] = useState(true);

  const [events, setEvents] = useState<AdminEventDTO[]>([]);
  const [eventPage, setEventPage] = useState(0);
  const [eventQuery, setEventQuery] = useState("");
  const [eventsLoading, setEventsLoading] = useState(false);
  const [eventsHasMore, setEventsHasMore] = useState(true);

  const [orders, setOrders] = useState<AdminOrderDTO[]>([]);
  const [orderPage, setOrderPage] = useState(0);
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [ordersHasMore, setOrdersHasMore] = useState(true);

  const isAdmin = globalData?.isAdmin ?? false;

  const tabButtons = useMemo(
    () => [
      { key: "users" as const, icon: "bi-people", text: "Usuarios" },
      { key: "courses" as const, icon: "bi-journal-text", text: "Cursos" },
      { key: "events" as const, icon: "bi-calendar-event", text: "Eventos" },
      { key: "orders" as const, icon: "bi-receipt", text: "Pedidos" },
    ],
    []
  );

  async function loadUsers(reset = false, query = userQuery) {
    try {
      startRequest();
      setUsersLoading(true);
      setError(null);
      const pageToLoad = reset ? 0 : userPage;
      const data = await getAdminUsers(pageToLoad, query);
      setUsers((prev) => (reset ? data : [...prev, ...data]));
      setUserPage(pageToLoad + 1);
      setUsersHasMore(data.length === PAGE_SIZE);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar usuarios");
    } finally {
      setUsersLoading(false);
      endRequest();
    }
  }

  async function loadCourses(reset = false, query = courseQuery) {
    try {
      startRequest();
      setCoursesLoading(true);
      setError(null);
      const pageToLoad = reset ? 0 : coursePage;
      const data = await getAdminCourses(pageToLoad, query);
      setCourses((prev) => (reset ? data : [...prev, ...data]));
      setCoursePage(pageToLoad + 1);
      setCoursesHasMore(data.length === PAGE_SIZE);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar cursos");
    } finally {
      setCoursesLoading(false);
      endRequest();
    }
  }

  async function loadEvents(reset = false, query = eventQuery) {
    try {
      startRequest();
      setEventsLoading(true);
      setError(null);
      const pageToLoad = reset ? 0 : eventPage;
      const data = await getAdminEvents(pageToLoad, query);
      setEvents((prev) => (reset ? data : [...prev, ...data]));
      setEventPage(pageToLoad + 1);
      setEventsHasMore(data.length === PAGE_SIZE);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar eventos");
    } finally {
      setEventsLoading(false);
      endRequest();
    }
  }

  async function loadOrders(reset = false) {
    try {
      startRequest();
      setOrdersLoading(true);
      setError(null);
      const pageToLoad = reset ? 0 : orderPage;
      const data = await getAdminOrders(pageToLoad);
      setOrders((prev) => (reset ? data : [...prev, ...data]));
      setOrderPage(pageToLoad + 1);
      setOrdersHasMore(data.length === PAGE_SIZE);
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudieron cargar pedidos");
    } finally {
      setOrdersLoading(false);
      endRequest();
    }
  }

  useEffect(() => {
    if (!isAdmin) return;

    if (activeTab === "users" && users.length === 0 && !usersLoading) {
      void loadUsers(true);
    }
    if (activeTab === "courses" && courses.length === 0 && !coursesLoading) {
      void loadCourses(true);
    }
    if (activeTab === "events" && events.length === 0 && !eventsLoading) {
      void loadEvents(true);
    }
    if (activeTab === "orders" && orders.length === 0 && !ordersLoading) {
      void loadOrders(true);
    }
  }, [activeTab, isAdmin]);

  async function handleUserStatusChange(user: AdminUserDTO) {
    try {
      startRequest();
      await updateAdminUserStatus(user.id, !user.isActive);
      setUsers((prev) => prev.map((it) => (it.id === user.id ? { ...it, isActive: !it.isActive } : it)));
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo actualizar el estado del usuario");
    } finally {
      endRequest();
    }
  }

  async function handleCourseStatusChange(course: AdminCourseDTO, publish: boolean) {
    try {
      startRequest();
      await updateAdminCourseStatus(course.id, publish ? "PUBLISHED" : "DRAFT");
      setCourses((prev) => prev.map((it) => (it.id === course.id ? { ...it, status: publish ? "PUBLISHED" : "DRAFT" } : it)));
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo actualizar el estado del curso");
    } finally {
      endRequest();
    }
  }

  async function handleEventStatusChange(event: AdminEventDTO, publish: boolean) {
    try {
      startRequest();
      await updateAdminEventStatus(event.id, publish ? "PUBLISHED" : "DRAFT");
      setEvents((prev) => prev.map((it) => (it.id === event.id ? { ...it, status: publish ? "PUBLISHED" : "DRAFT" } : it)));
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo actualizar el estado del evento");
    } finally {
      endRequest();
    }
  }

  function switchTab(tab: TabKey) {
    setSearchParams({ tab }, { replace: true });
  }

  if (!isAdmin) {
    return (
      <main className="main">
        <div className="page-title light-background">
          <div className="container">
            <nav className="breadcrumbs">
              <ol>
                <li>
                  <Link to="/new">Inicio</Link>
                </li>
                <li className="current">Dashboard</li>
              </ol>
            </nav>
            <h1>Dashboard de Administrador</h1>
          </div>
        </div>

        <section className="section">
          <div className="container">
            <div className="alert alert-warning" role="alert">
              No tienes permisos para acceder al dashboard de administrador.
            </div>
          </div>
        </section>
      </main>
    );
  }

  return (
    <main className="main">
      <div className="page-title light-background">
        <div className="container">
          <nav className="breadcrumbs">
            <ol>
              <li>
                <Link to="/new">Inicio</Link>
              </li>
              <li className="current">Dashboard</li>
            </ol>
          </nav>
          <h1>Dashboard de Administrador</h1>
        </div>
      </div>

      <section id="admin-dashboard-section" className="section service-details">
        <div className="container" data-aos="fade-up">
          {error && (
            <div className="alert alert-warning" role="alert">
              {error}
            </div>
          )}

          <div className="service-tabs">
            <ul className="nav nav-tabs mb-4" role="tablist">
              {tabButtons.map((tab) => (
                <li key={tab.key} className="nav-item" role="presentation">
                  <button
                    className={`nav-link ${activeTab === tab.key ? "active" : ""}`}
                    type="button"
                    role="tab"
                    onClick={() => switchTab(tab.key)}
                  >
                    <i className={`bi ${tab.icon} me-2`} />
                    {tab.text}
                  </button>
                </li>
              ))}
            </ul>

            {activeTab === "users" && (
              <>
                <div className="card border-0 shadow-sm mb-4">
                  <div className="card-body p-4">
                    <form
                      onSubmit={(e) => {
                        e.preventDefault();
                        void loadUsers(true, userQuery);
                      }}
                    >
                      <div className="input-group">
                        <span className="input-group-text bg-white border-end-0">
                          <i className="bi bi-search" />
                        </span>
                        <input
                          type="text"
                          className="form-control border-start-0"
                          placeholder="Buscar usuario por nombre..."
                          value={userQuery}
                          onChange={(e) => setUserQuery(e.target.value)}
                        />
                        <button className="btn btn-accent" type="submit" disabled={usersLoading}>
                          Buscar
                        </button>
                        <button
                          className="btn btn-accent-outline"
                          type="button"
                          onClick={() => {
                            setUserQuery("");
                            void loadUsers(true, "");
                          }}
                        >
                          Limpiar
                        </button>
                      </div>
                    </form>
                  </div>
                </div>

                <div className="card border-0 shadow-sm">
                  <div className="card-body p-0">
                    <div className="table-responsive">
                      <table className="table table-hover align-middle mb-0">
                        <thead className="table-light">
                          <tr>
                            <th>Usuario</th>
                            <th>Email</th>
                            <th>Suscripción</th>
                            <th>Estado</th>
                            <th className="text-end">Acciones</th>
                          </tr>
                        </thead>
                        <tbody>
                          {users.map((user) => (
                            <tr key={user.id}>
                              <td>
                                <div className="d-flex align-items-center gap-2">
                                  <i className="bi bi-person-circle fs-4 text-muted" />
                                  <span className="fw-semibold">{user.username}</span>
                                </div>
                              </td>
                              <td className="text-muted">{user.email}</td>
                              <td>
                                <span className={`badge ${user.isSubscribed ? "bg-success" : "bg-secondary"}`}>
                                  {user.isSubscribed ? "Sí" : "No"}
                                </span>
                              </td>
                              <td>
                                <span className={`badge ${user.isActive ? "bg-success" : "bg-danger"}`}>
                                  {user.isActive ? "Activo" : "Baneado"}
                                </span>
                              </td>
                              <td className="text-end">
                                <div className="d-flex justify-content-end align-items-start gap-2 flex-nowrap">
                                  <Link to={`/new/profile/${user.id}`} className="btn btn-sm btn-accent-outline">
                                    Ver perfil
                                  </Link>
                                  <button
                                    type="button"
                                    className={`btn btn-sm ${user.isActive ? "btn-accent-outline" : "btn-accent"}`}
                                    onClick={() => void handleUserStatusChange(user)}
                                  >
                                    <i className={`bi ${user.isActive ? "bi-slash-circle" : "bi-check-circle"} me-1`} />
                                    {user.isActive ? "Banear" : "Desbanear"}
                                  </button>
                                </div>
                              </td>
                            </tr>
                          ))}
                          {!usersLoading && users.length === 0 && (
                            <tr>
                              <td colSpan={5} className="text-center text-muted py-4">
                                No se encontraron usuarios.
                              </td>
                            </tr>
                          )}
                        </tbody>
                      </table>
                    </div>
                    {usersHasMore && (
                      <div className="text-center p-3 border-top">
                        <button className="btn btn-accent btn-sm" type="button" disabled={usersLoading} onClick={() => void loadUsers(false)}>
                          {usersLoading ? "Cargando..." : "Cargar más usuarios"}
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              </>
            )}

            {activeTab === "courses" && (
              <>
                <div className="card border-0 shadow-sm mb-4">
                  <div className="card-body p-4">
                    <form
                      onSubmit={(e) => {
                        e.preventDefault();
                        void loadCourses(true, courseQuery);
                      }}
                    >
                      <div className="input-group">
                        <span className="input-group-text bg-white border-end-0">
                          <i className="bi bi-search" />
                        </span>
                        <input
                          type="text"
                          className="form-control border-start-0"
                          placeholder="Buscar curso por título..."
                          value={courseQuery}
                          onChange={(e) => setCourseQuery(e.target.value)}
                        />
                        <button className="btn btn-accent" type="submit" disabled={coursesLoading}>
                          Buscar
                        </button>
                        <button
                          className="btn btn-accent-outline"
                          type="button"
                          onClick={() => {
                            setCourseQuery("");
                            void loadCourses(true, "");
                          }}
                        >
                          Limpiar
                        </button>
                      </div>
                    </form>
                  </div>
                </div>

                <div className="card border-0 shadow-sm">
                  <div className="card-body p-0">
                    <div className="table-responsive">
                      <table className="table table-hover align-middle mb-0">
                        <thead className="table-light">
                          <tr>
                            <th>Curso</th>
                            <th>Estado</th>
                            <th className="text-end">Acciones</th>
                          </tr>
                        </thead>
                        <tbody>
                          {courses.map((course) => {
                            const isPending = course.status === "PENDING_REVIEW";
                            return (
                              <tr key={course.id} className={isPending ? "table-warning" : ""}>
                                <td>
                                  <div>
                                    <span className="fw-semibold">{course.title}</span>
                                    {isPending && <span className="badge bg-light text-accent ms-2">En revisión</span>}
                                    <p className="text-muted small mb-0">{course.shortDescription}</p>
                                  </div>
                                </td>
                                <td>
                                  <span className={`badge ${isPending ? "bg-light text-accent" : "bg-secondary"}`}>
                                    {isPending ? "Pendiente" : course.status}
                                  </span>
                                </td>
                                <td className="text-end">
                                  <div className="d-flex justify-content-end align-items-start gap-2 flex-wrap">
                                    <a href={`/new/course/${course.id}`} className="btn btn-sm btn-accent-outline">
                                      <i className="bi bi-eye" /> Ver
                                    </a>
                                    <a href={`/new/course/${course.id}/edit`} className="btn btn-sm btn-accent-outline">
                                      <i className="bi bi-pencil" /> Editar
                                    </a>
                                    {isPending && (
                                      <>
                                        <button type="button" className="btn btn-sm btn-accent" onClick={() => void handleCourseStatusChange(course, true)}>
                                          <i className="bi bi-check-lg" /> Aprobar
                                        </button>
                                        <button type="button" className="btn btn-sm btn-accent-outline" onClick={() => void handleCourseStatusChange(course, false)}>
                                          <i className="bi bi-x-lg" /> Rechazar
                                        </button>
                                      </>
                                    )}
                                  </div>
                                </td>
                              </tr>
                            );
                          })}
                          {!coursesLoading && courses.length === 0 && (
                            <tr>
                              <td colSpan={3} className="text-center text-muted py-4">
                                No se encontraron cursos.
                              </td>
                            </tr>
                          )}
                        </tbody>
                      </table>
                    </div>
                    {coursesHasMore && (
                      <div className="text-center p-3 border-top">
                        <button className="btn btn-accent btn-sm" type="button" disabled={coursesLoading} onClick={() => void loadCourses(false)}>
                          {coursesLoading ? "Cargando..." : "Cargar más cursos"}
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              </>
            )}

            {activeTab === "events" && (
              <>
                <div className="card border-0 shadow-sm mb-4">
                  <div className="card-body p-4">
                    <form
                      onSubmit={(e) => {
                        e.preventDefault();
                        void loadEvents(true, eventQuery);
                      }}
                    >
                      <div className="input-group">
                        <span className="input-group-text bg-white border-end-0">
                          <i className="bi bi-search" />
                        </span>
                        <input
                          type="text"
                          className="form-control border-start-0"
                          placeholder="Buscar evento por título..."
                          value={eventQuery}
                          onChange={(e) => setEventQuery(e.target.value)}
                        />
                        <button className="btn btn-accent" type="submit" disabled={eventsLoading}>
                          Buscar
                        </button>
                        <button
                          className="btn btn-accent-outline"
                          type="button"
                          onClick={() => {
                            setEventQuery("");
                            void loadEvents(true, "");
                          }}
                        >
                          Limpiar
                        </button>
                      </div>
                    </form>
                  </div>
                </div>

                <div className="card border-0 shadow-sm">
                  <div className="card-body p-0">
                    <div className="table-responsive">
                      <table className="table table-hover align-middle mb-0">
                        <thead className="table-light">
                          <tr>
                            <th>Evento</th>
                            <th>Estado</th>
                            <th className="text-end">Acciones</th>
                          </tr>
                        </thead>
                        <tbody>
                          {events.map((event) => {
                            const isPending = event.status === "PENDING_REVIEW";
                            return (
                              <tr key={event.id} className={isPending ? "table-warning" : ""}>
                                <td>
                                  <div>
                                    <span className="fw-semibold">{event.title}</span>
                                    {isPending && <span className="badge bg-light text-accent ms-2">En revisión</span>}
                                    <p className="text-muted small mb-0">{event.category}</p>
                                  </div>
                                </td>
                                <td>
                                  <span className={`badge ${isPending ? "bg-light text-accent" : "bg-secondary"}`}>
                                    {isPending ? "Pendiente" : event.status}
                                  </span>
                                </td>
                                <td className="text-end">
                                  <div className="d-flex justify-content-end align-items-start gap-2 flex-wrap">
                                    <a href={`/new/event/${event.id}`} className="btn btn-sm btn-accent-outline">
                                      <i className="bi bi-eye" /> Ver
                                    </a>
                                    <a href={`/new/event/${event.id}/edit`} className="btn btn-sm btn-accent-outline">
                                      <i className="bi bi-pencil" /> Editar
                                    </a>
                                    {isPending && (
                                      <>
                                        <button type="button" className="btn btn-sm btn-accent" onClick={() => void handleEventStatusChange(event, true)}>
                                          <i className="bi bi-check-lg" /> Aprobar
                                        </button>
                                        <button type="button" className="btn btn-sm btn-accent-outline" onClick={() => void handleEventStatusChange(event, false)}>
                                          <i className="bi bi-x-lg" /> Rechazar
                                        </button>
                                      </>
                                    )}
                                  </div>
                                </td>
                              </tr>
                            );
                          })}
                          {!eventsLoading && events.length === 0 && (
                            <tr>
                              <td colSpan={3} className="text-center text-muted py-4">
                                No se encontraron eventos.
                              </td>
                            </tr>
                          )}
                        </tbody>
                      </table>
                    </div>
                    {eventsHasMore && (
                      <div className="text-center p-3 border-top">
                        <button className="btn btn-accent btn-sm" type="button" disabled={eventsLoading} onClick={() => void loadEvents(false)}>
                          {eventsLoading ? "Cargando..." : "Cargar más eventos"}
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              </>
            )}

            {activeTab === "orders" && (
              <div className="card border-0 shadow-sm">
                <div className="card-body p-0">
                  <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                      <thead className="table-light">
                        <tr>
                          <th>Pedido</th>
                          <th>Usuario</th>
                          <th>Estado</th>
                          <th>Fecha</th>
                          <th className="text-end">Total</th>
                        </tr>
                      </thead>
                      <tbody>
                        {orders.map((order) => (
                          <tr key={order.id}>
                            <td className="fw-semibold">#{order.id}</td>
                            <td>
                              <Link to={`/new/profile/${order.userId}`}>Usuario #{order.userId}</Link>
                            </td>
                            <td>
                              <span className="badge bg-secondary">{order.status}</span>
                            </td>
                            <td>{formatDate(order.createdAt)}</td>
                            <td className="text-end fw-semibold">{order.totalAmountEuros} €</td>
                          </tr>
                        ))}
                        {!ordersLoading && orders.length === 0 && (
                          <tr>
                            <td colSpan={5} className="text-center text-muted py-4">
                              No hay pedidos registrados.
                            </td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>
                  {ordersHasMore && (
                    <div className="text-center p-3 border-top">
                      <button className="btn btn-accent btn-sm" type="button" disabled={ordersLoading} onClick={() => void loadOrders(false)}>
                        {ordersLoading ? "Cargando..." : "Cargar más pedidos"}
                      </button>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      </section>
    </main>
  );
}
