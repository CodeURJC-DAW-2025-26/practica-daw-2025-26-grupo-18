import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router";

import { getProfileById, updateProfile } from "~/services/profileService";
import type { ProfileDTO, ProfileUpdateDTO } from "~/dtos/ProfileDTO";
import { getUserProfileImageUrl } from "~/utils/imageUrls";
import Chart from "../components/Chart";
import { GET_COURSE_PROGRESS, GET_LESSONS_LEARNED, GET_CREATED_COURSE_STATUS } from "../constants/constants";

type ProfileFormState = {
  username: string;
  email: string;
  country: string;
  shortDescription: string;
  currentGoal: string;
  weeklyRoutine: string;
  comunity: string;
};

function getStringValue(record: Record<string, unknown>, keys: string[]): string | null {
  for (const key of keys) {
    const value = record[key];
    if (typeof value === "string" && value.trim()) return value;
  }
  return null;
}

function getNumberValue(record: Record<string, unknown>, keys: string[]): number | null {
  for (const key of keys) {
    const value = record[key];
    if (typeof value === "number") return value;
  }
  return null;
}

export default function ProfileRoute() {
  const params = useParams();
  const userId = Number(params.id);

  const [profile, setProfile] = useState<ProfileDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isEditing, setIsEditing] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const [selectedImageFile, setSelectedImageFile] = useState<File | undefined>(undefined);
  const [previewImageUrl, setPreviewImageUrl] = useState<string | null>(null);

  const [form, setForm] = useState<ProfileFormState>({
    username: "",
    email: "",
    country: "",
    shortDescription: "",
    currentGoal: "",
    weeklyRoutine: "",
    comunity: "",
  });

  const profileImageUrl = useMemo(() => {
    if (!profile) return "/services/default_avatar.png";
    return getUserProfileImageUrl(profile.id);
  }, [profile]);

  useEffect(() => {
    if (!Number.isFinite(userId) || userId <= 0) {
      setError("Perfil no válido");
      setLoading(false);
      return;
    }

    let cancelled = false;

    async function fetchProfile() {
      try {
        setLoading(true);
        setError(null);
        const data = await getProfileById(userId);
        if (cancelled) return;

        setProfile(data);
        setForm({
          username: data.username ?? "",
          email: data.email ?? "",
          country: data.country ?? "",
          shortDescription: data.shortDescription ?? "",
          currentGoal: data.currentGoal ?? "",
          weeklyRoutine: data.weeklyRoutine ?? "",
          comunity: data.comunity ?? "",
        });
      } catch (err) {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : "No se pudo cargar el perfil");
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    void fetchProfile();

    return () => {
      cancelled = true;
    };
  }, [userId]);

  function updateFormField(field: keyof ProfileFormState, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  function resetEditState() {
    setIsEditing(false);
    setFormError(null);
    setSelectedImageFile(undefined);
    setPreviewImageUrl(null);
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!profile) return;

    const payload: ProfileUpdateDTO = {
      username: form.username,
      email: form.email,
      country: form.country,
      shortDescription: form.shortDescription,
      currentGoal: form.currentGoal,
      weeklyRoutine: form.weeklyRoutine,
      comunity: form.comunity,
    };

    try {
      setIsSaving(true);
      setFormError(null);
      await updateProfile(profile.id, payload, selectedImageFile);

      const refreshed = await getProfileById(profile.id);
      setProfile(refreshed);
      setForm({
        username: refreshed.username ?? "",
        email: refreshed.email ?? "",
        country: refreshed.country ?? "",
        shortDescription: refreshed.shortDescription ?? "",
        currentGoal: refreshed.currentGoal ?? "",
        weeklyRoutine: refreshed.weeklyRoutine ?? "",
        comunity: refreshed.comunity ?? "",
      });

      resetEditState();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No se pudo actualizar el perfil");
    } finally {
      setIsSaving(false);
    }
  }

  function handleImageChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    setSelectedImageFile(file);

    if (!file) {
      setPreviewImageUrl(null);
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result === "string") {
        setPreviewImageUrl(reader.result);
      }
    };
    reader.readAsDataURL(file);
  }

  if (loading) {
    return (
      <main className="main" id="profile-page-main">
        <section className="section">
          <div className="container">
            <div className="text-center py-5">Cargando perfil...</div>
          </div>
        </section>
      </main>
    );
  }

  if (error || !profile) {
    return (
      <main className="main" id="profile-page-main">
        <section className="section">
          <div className="container">
            <div className="alert alert-warning" role="alert">
              {error || "No se encontró el perfil"}
            </div>
          </div>
        </section>
      </main>
    );
  }

  return (
    <main className={`main ${isEditing ? "profile-edit-mode" : ""}`} id="profile-page-main">
      <div className="page-title light-background">
        <div className="container">
          <nav className="breadcrumbs">
            <ol>
              <li>
                <Link to="/new">Inicio</Link>
              </li>
              <li className="current">Perfil</li>
            </ol>
          </nav>
          <h1>Perfil de estudiante</h1>
        </div>
      </div>

      <section className="section profile-overview-section">
        <div className="container" data-aos="fade-up">
          {formError && (
            <div className="alert alert-warning mb-4" role="alert">
              <i className="bi bi-exclamation-triangle-fill me-2" />
              {formError}
            </div>
          )}

          <div className="row g-4" id="profile-overview-row">
            <div className={isEditing ? "col-12 is-editing" : "col-lg-4"} id="profile-main-container">
              <div className="card border-0 shadow-sm bg-light h-100">
                <div className="card-body p-4 text-center">
                  {!isEditing && (
                    <div id="profile-view">
                      <div className="profile-avatar-wrap">
                        <img src={profileImageUrl} alt="Foto de perfil" className="rounded-circle mb-3" width={120} height={120} />
                      </div>
                      <h3 className="h5 fw-bold mb-1">{profile.username}</h3>
                      <p className="text-muted mb-1">@{profile.username}</p>
                      <p className="text-muted mb-3">{profile.userType}</p>
                      <div className="course-tags justify-content-center">
                        {profile.userTags.length > 0 ? (
                          profile.userTags.map((tag) => (
                            <span key={tag} className="course-tag">
                              {tag}
                            </span>
                          ))
                        ) : (
                          <span className="text-muted small">Sin cursos suscritos</span>
                        )}
                      </div>
                      <hr className="my-4" />
                      <div className="text-start">
                        <p className="mb-2">
                          <i className="bi bi-envelope me-2" /> {profile.email}
                        </p>
                        <p className="mb-2">
                          <i className="bi bi-geo-alt me-2" /> {profile.country || "Sin país"}
                        </p>
                        <p className="mb-0">
                          <i className="bi bi-calendar-event me-2" /> Miembro desde 2024
                        </p>
                      </div>
                    </div>
                  )}

                  {profile.profileOwner && isEditing && (
                    <div id="profile-edit" style={{ display: "block" }}>
                      <form className="needs-validation profile-edit-form" onSubmit={handleSubmit} noValidate>
                        <div className="text-start mb-3">
                          <h4 className="h6 fw-bold mb-1">Editar perfil</h4>
                          <p className="text-muted small mb-0">Actualiza tus datos para que tu perfil destaque más.</p>
                        </div>

                        <div className="mb-3 profile-avatar-editor">
                          <img
                            id="preview-img"
                            src={previewImageUrl || profileImageUrl}
                            alt="Foto de perfil"
                            className="rounded-circle mb-2"
                            width={120}
                            height={120}
                          />
                          <div className="profile-avatar-input-wrap">
                            <label htmlFor="imageFile" className="form-label small text-muted">
                              Cambiar imagen
                            </label>
                            <input type="file" className="form-control form-control-sm" id="imageFile" name="imageFile" accept="image/*" onChange={handleImageChange} />
                          </div>
                        </div>

                        <div className="mb-3 text-start">
                          <label htmlFor="username" className="form-label fw-semibold">
                            <i className="bi bi-person me-2" />Nombre de usuario
                          </label>
                          <input type="text" className="form-control" id="username" name="username" value={form.username} onChange={(e) => updateFormField("username", e.target.value)} required />
                        </div>

                        <div className="mb-3 text-start">
                          <label htmlFor="email" className="form-label fw-semibold">
                            <i className="bi bi-envelope me-2" />Email
                          </label>
                          <input type="email" className="form-control" id="email" name="email" value={form.email} onChange={(e) => updateFormField("email", e.target.value)} required />
                        </div>

                        <div className="mb-3 text-start">
                          <label htmlFor="country" className="form-label fw-semibold">
                            <i className="bi bi-geo-alt me-2" />País
                          </label>
                          <input type="text" className="form-control" id="country" name="country" value={form.country} onChange={(e) => updateFormField("country", e.target.value)} />
                        </div>

                        <div className="mb-3 text-start">
                          <label htmlFor="shortDescription" className="form-label fw-semibold">
                            <i className="bi bi-card-text me-2" />Sobre mí
                          </label>
                          <textarea className="form-control" id="shortDescription" name="shortDescription" rows={3} value={form.shortDescription} onChange={(e) => updateFormField("shortDescription", e.target.value)} />
                        </div>

                        <div className="mb-3 text-start">
                          <label htmlFor="currentGoal" className="form-label fw-semibold">
                            <i className="bi bi-briefcase me-2" />Objetivo actual
                          </label>
                          <input type="text" className="form-control" id="currentGoal" name="currentGoal" value={form.currentGoal} onChange={(e) => updateFormField("currentGoal", e.target.value)} />
                        </div>

                        <div className="mb-3 text-start">
                          <label htmlFor="weeklyRoutine" className="form-label fw-semibold">
                            <i className="bi bi-lightning me-2" />Rutina semanal
                          </label>
                          <input type="text" className="form-control" id="weeklyRoutine" name="weeklyRoutine" value={form.weeklyRoutine} onChange={(e) => updateFormField("weeklyRoutine", e.target.value)} />
                        </div>

                        <div className="mb-3 text-start">
                          <label htmlFor="comunity" className="form-label fw-semibold">
                            <i className="bi bi-people me-2" />Comunidad
                          </label>
                          <input type="text" className="form-control" id="comunity" name="comunity" value={form.comunity} onChange={(e) => updateFormField("comunity", e.target.value)} />
                        </div>

                        <div className="d-flex gap-2 justify-content-center mt-3">
                          <button type="submit" className="btn btn-accent" disabled={isSaving}>
                            {isSaving ? "Guardando..." : "Guardar cambios"}
                          </button>
                          <button type="button" className="btn btn-accent-outline" onClick={resetEditState} disabled={isSaving}>
                            Cancelar
                          </button>
                        </div>
                      </form>
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="col-lg-8" id="profile-about-container">
              <div className="card border-0 shadow-sm bg-light h-100">
                <div className="card-body p-4">
                  <h3 className="h5 fw-bold mb-3">Sobre mí</h3>
                  <p className="text-muted">{profile.shortDescription || "Todavía no se ha añadido ninguna información."}</p>
                  <div className="row g-3">
                    <div className="col-md-6">
                      <div className="d-flex align-items-center gap-3">
                        <i className="bi bi-briefcase fs-4 text-accent" />
                        <div>
                          <p className="mb-1 fw-semibold">Objetivo actual</p>
                          <p className="mb-0 text-muted">{profile.currentGoal || "Todavía no se ha añadido ninguna información."}</p>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6">
                      <div className="d-flex align-items-center gap-3">
                        <i className="bi bi-lightning fs-4 text-accent" />
                        <div>
                          <p className="mb-1 fw-semibold">Rutina semanal</p>
                          <p className="mb-0 text-muted">{profile.weeklyRoutine || "Todavía no se ha añadido ninguna información."}</p>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6">
                      <div className="d-flex align-items-center gap-3">
                        <i className="bi bi-people fs-4 text-accent" />
                        <div>
                          <p className="mb-1 fw-semibold">Comunidad</p>
                          <p className="mb-0 text-muted">{profile.comunity || "Todavía no se ha añadido ninguna información."}</p>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6">
                      <div className="d-flex align-items-center gap-3">
                        <i className="bi bi-award fs-4 text-accent" />
                        <div>
                          <p className="mb-1 fw-semibold">Certificaciones</p>
                          <p className="mb-0 text-muted">{profile.completedCourses} completadas</p>
                        </div>
                      </div>
                    </div>
                  </div>

                  {profile.profileOwner && (
                    <div className="d-flex flex-wrap gap-2 mt-4">
                      <button type="button" className="btn btn-accent" onClick={() => setIsEditing(true)}>
                        Editar perfil
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {!isEditing && (
        <>
          <section id="features-cards" className="features-cards section profile-stats-section">
            <div className="container" data-aos="fade-up" data-aos-delay="100">
              <div className="row g-4">
                <div className="col-lg-4" data-aos="flip-left" data-aos-delay="100">
                  <div className="feature-card">
                    <div className="icon-badge">
                      <div className="icon-box icon-box-compact">
                        <i className="bi bi-journal-text" />
                      </div>
                      <span className="badge status-badge">ACTIVO</span>
                    </div>
                    <h3>Cursos en progreso</h3>
                    <p>Actualmente estas avanzando en {profile.inProgressCount} cursos. Total inscritos: {profile.totalEnrollments}.</p>
                    <div className="course-progress">
                      <small className="course-progress-label">Progreso global: {profile.averageProgress}%</small>
                      <div className="course-progress-bar">
                        <div className="course-progress-fill" style={{ width: `${Math.max(0, Math.min(profile.averageProgress, 100))}%` }} />
                      </div>
                      <div className="text-center mt-3 profile-stats-chart-frame profile-stats-chart-frame--tall" style={{ height: "300px" }}>
                        <Chart 
                          info={GET_COURSE_PROGRESS} 
                          infoUser={profile.id} 
                          infoCourse={0} 
                        />
                      </div>

                    </div>
                  </div>
                </div>

                <div className="col-lg-4" data-aos="flip-left" data-aos-delay="200">
                  <div className="feature-card">
                    <div className="icon-badge">
                      <div className="icon-box icon-box-compact">
                        <i className="bi bi-clock-history" />
                      </div>
                      <span className="badge status-badge">CONSTANCIA</span>
                    </div>
                    <h3>Lecciones aprendidas</h3>
                    <p>Has completado {profile.totalLessonsCompleted} lecciones en total.</p>
                    <div className="text-center mt-3 mb-3 profile-stats-chart-frame" style={{ height: "300px" }}>
                      <Chart 
                        info={GET_LESSONS_LEARNED} 
                        infoUser={profile.id} 
                        infoCourse={0} 
                      />
                    </div>
                    <ul className="feature-list">
                      <li>
                        <i className="bi bi-check-circle" /> {profile.completedLessonsThisMonth} lecciones este mes
                      </li>
                      <li>
                        <i className="bi bi-check-circle" /> {profile.averageLessonsPerMonth} lecciones/mes (promedio)
                      </li>
                    </ul>
                  </div>
                </div>

                <div className="col-lg-4" data-aos="flip-left" data-aos-delay="300">
                  <div className="feature-card">
                    <div className="icon-badge">
                      <div className="icon-box icon-box-compact">
                        <i className="bi bi-award" />
                      </div>
                      <span className="badge status-badge">LOGROS</span>
                    </div>
                    <h3>Certificados</h3>
                    <p>{profile.completedCourses} certificados completados y {profile.inProgressCount} cursos en progreso.</p>
                    <ul className="feature-list">
                      {profile.completedCourseNames.length > 0 ? (
                        profile.completedCourseNames.map((name) => (
                          <li key={name}>
                            <i className="bi bi-check-circle" /> {name}
                          </li>
                        ))
                      ) : (
                        <li className="text-muted">
                          <i className="bi bi-info-circle" /> Aun no has completado ningun curso
                        </li>
                      )}
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </section>
          {profile.createdCourses.length > 0 && (
            <section className="section bg-light instructor-panel-section">
              <div className="container" data-aos="fade-up">
                <div className="section-title section-title-compact">
                  <h2>Panel de Instructor</h2>
                  <p>Gestiona tus cursos creados y visualiza el progreso de tus estudiantes.</p>
                </div>

                <div className="row g-4 align-items-stretch">
                  <div className="col-lg-8 d-flex flex-column">
                    <div id="instructorCoursesCarousel" className="carousel slide flex-grow-1 instructor-carousel w-100" data-bs-ride="false">
                      <div className="carousel-inner h-100">
                        {profile.createdCourses.map((course, index) => {
                          const id = getNumberValue(course, ["id", "courseId"]);
                          const title = getStringValue(course, ["title", "name"]) || `Curso #${index + 1}`;
                          const status = getStringValue(course, ["status"]);
                          const totalSubscribers = getNumberValue(course, ["totalSubscribers", "subscribers"]);
                          const monthlySubscribers = getNumberValue(course, ["monthlySubscribers", "subscribersThisMonth"]);
                          const hasSubscribers = (totalSubscribers ?? 0) > 0;

                          return (
                            <div className={`carousel-item ${index === 0 ? "active" : ""} h-100`} key={`${id ?? "created-course"}-${index}`}>
                              <div className="card border-0 shadow-sm h-100 instructor-course-card instructor-course-card-modern">
                                <div className="card-body p-4">
                                  <div className="d-flex justify-content-between align-items-center mb-3">
                                    <h4 className="h5 fw-bold mb-0 text-truncate instructor-course-title" title={title}>
                                      {title}
                                    </h4>
                                    {status ? <span className="badge bg-success">{status}</span> : <span className="badge bg-secondary">Borrador</span>}
                                  </div>
                                  <div className="row align-items-center">
                                    <div className="col-md-5">
                                      <div className="mb-3">
                                        <p className="text-muted small mb-1">
                                          <i className="bi bi-people me-1" /> Total de usuarios suscritos: <span className="fw-semibold text-dark">{totalSubscribers ?? 0}</span>
                                        </p>
                                        <p className="text-muted small mb-0">
                                          <i className="bi bi-person-plus me-1" /> Usuarios suscritos este mes: <span className="fw-semibold text-dark">{monthlySubscribers ?? 0}</span>
                                        </p>
                                      </div>
                                      {id && (
                                        <a href={`/new/course/${id}/edit`} className="btn btn-sm btn-accent mt-2">
                                          Gestionar curso
                                        </a>
                                      )}
                                    </div>
                                    <div className="col-md-7 text-center">
                                      {hasSubscribers && id ? (
                                        <div className="instructor-chart-frame">
                                          <Chart
                                            info={GET_CREATED_COURSE_STATUS}
                                            infoUser={profile.id}
                                            infoCourse={id}
                                          />
                                        </div>
                                      ) : (
                                        <div className="instructor-chart-frame d-flex flex-column align-items-center justify-content-center text-center bg-light rounded" style={{ minHeight: 200 }}>
                                          <i className="bi bi-people text-muted mb-2" style={{ fontSize: "2rem", opacity: 0.5 }} />
                                          <h6 className="fw-bold text-dark mb-1">Aún no hay nadie en el curso</h6>
                                          <p className="text-muted small mb-0">Comparte tu curso para conseguir estudiantes.</p>
                                        </div>
                                      )}
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          );
                        })}
                      </div>

                      {profile.createdCourses.length > 1 && (
                        <>
                          <button className="carousel-control-prev instructor-carousel-control" type="button" data-bs-target="#instructorCoursesCarousel" data-bs-slide="prev">
                            <span className="carousel-control-prev-icon" aria-hidden="true" />
                            <span className="visually-hidden">Anterior</span>
                          </button>
                          <button className="carousel-control-next instructor-carousel-control" type="button" data-bs-target="#instructorCoursesCarousel" data-bs-slide="next">
                            <span className="carousel-control-next-icon" aria-hidden="true" />
                            <span className="visually-hidden">Siguiente</span>
                          </button>
                        </>
                      )}
                    </div>
                  </div>

                  <div className="col-lg-4 d-flex flex-column">
                    <div className="card border-0 shadow-sm flex-grow-1 w-100 instructor-create-card">
                      <div className="card-body p-4 d-flex align-items-center justify-content-center border-dashed instructor-create-card-body">
                        <div className="text-center py-4">
                          <div className="icon-box mb-3 mx-auto text-muted">
                            <i className="bi bi-plus-lg fs-1" />
                          </div>
                          <h5 className="fw-bold text-muted">Crear nuevo curso</h5>
                          <a href="/new/courses/new" className="stretched-link" aria-label="Crear nuevo curso" />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </section>
          )}
          <section className="courses-catalog section">
            <div className="container" data-aos="fade-up">
              <div className="section-title section-title-compact">
                <h2>Mis cursos suscritos</h2>
                <p>Continua donde lo dejaste y completa tus rutas activas.</p>
              </div>

              <div className="course-list">
                {profile.subscribedCourses.length > 0 ? (
                  profile.subscribedCourses.map((course, index) => {
                    const id = getNumberValue(course, ["id", "courseId"]);
                    const title = getStringValue(course, ["title", "name"]) || `Curso #${index + 1}`;
                    const description = getStringValue(course, ["description", "shortDescription"]) || "";
                    const creatorName = getStringValue(course, ["creatorName", "authorName", "teacherName"]) || "";
                    const progress = Math.max(0, Math.min(getNumberValue(course, ["progress"]) ?? 0, 100));
                    const tagsValue = (course as Record<string, unknown>).tags;
                    const tags = Array.isArray(tagsValue) ? tagsValue.filter((tag): tag is string => typeof tag === "string") : [];

                    return (
                      <article className="course-card-full" data-aos="fade-up" key={`${id ?? "course"}-${index}`}>
                        <div className="course-card-header">
                          <div>
                            <h3 className="course-card-title">{title}</h3>
                            <div className="course-tags">
                              {tags.map((tag) => (
                                <span className="course-tag" key={tag}>
                                  {tag}
                                </span>
                              ))}
                            </div>
                          </div>
                          <div className="course-card-price">
                            <span className="badge" style={{ backgroundColor: "var(--accent-color)", color: "#fff" }}>
                              En curso · {progress}%
                            </span>
                          </div>
                        </div>
                        <p className="course-card-desc">{description}</p>
                        <div className="course-card-meta">
                          <div>
                            <i className="bi bi-person" /> {creatorName}
                          </div>
                        </div>
                        {id && (
                          <div className="course-card-actions">
                            <a href={`/new/course/${id}`} className="btn btn-sm btn-accent-outline course-card-btn">
                              Ver detalles
                            </a>
                          </div>
                        )}
                      </article>
                    );
                  })
                ) : (
                  <p className="text-muted text-center">No estas suscrito a ningun curso todavia.</p>
                )}
              </div>
            </div>
          </section>

          <section className="section">
            <div className="container" data-aos="fade-up">
              <div className="section-title section-title-compact">
                <h2>Eventos guardados</h2>
                <p>Actividades que has marcado para ampliar tu red profesional.</p>
              </div>

              <div className="list-group">
                {profile.userEvents.length > 0 ? (
                  profile.userEvents.map((event, index) => {
                    const id = getNumberValue(event, ["id", "eventId"]);
                    const title = getStringValue(event, ["title", "name"]) || `Evento #${index + 1}`;
                    const locationLabel = getStringValue(event, ["locationLabel", "location", "city"]);
                    const startDate = getStringValue(event, ["startDate", "date"]);

                    return id ? (
                      <a
                        href={`/new/event/${id}`}
                        className="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                        key={`${id ?? "event"}-${index}`}>
                        <div>
                          <h6 className="mb-1">{title}</h6>
                          <small className="text-muted">
                            <i className="bi bi-geo-alt me-1" /> {locationLabel || ""}
                          </small>
                        </div>
                        <span className="badge bg-light text-dark border">{startDate || ""}</span>
                      </a>
                    ) : (
                      <div className="list-group-item" key={`${id ?? "event"}-${index}`}>
                        {title}
                      </div>
                    );
                  })
                ) : (
                  <p className="text-muted text-center">No tienes eventos guardados.</p>
                )}
              </div>
            </div>
          </section>
        </>
      )}
    </main>
  );
}
