import React, { useState } from "react";
import { Form, Button, Row, Col, InputGroup, Card } from "react-bootstrap";
import type { CourseDTO, ModuleDTO, LessonDTO } from "~/dtos/CourseDTO";

interface CourseFormProps {
  initialData?: Partial<CourseDTO>;
  onSubmit: (data: any, imageFile?: File) => void;
  isSubmitting?: boolean;
}

export default function CourseForm({ initialData, onSubmit, isSubmitting }: CourseFormProps) {
  const [validated, setValidated] = useState(false);
  const [formData, setFormData] = useState<Partial<CourseDTO>>({
    title: initialData?.title || "",
    shortDescription: initialData?.shortDescription || "",
    longDescription: initialData?.longDescription || "",
    price: initialData?.price || 0,
    priceCents: initialData?.priceCents || 0,
    videoHours: initialData?.videoHours || 0,
    downloadableResources: initialData?.downloadableResources || 0,
    language: initialData?.language || "Español",
    status: initialData?.status || "DRAFT",
    learningPoints: initialData?.learningPoints || [""],
    prerequisites: initialData?.prerequisites || [""],
    modules: initialData?.modules || [],
  });
  const [tagsString, setTagsString] = useState<string>(
    initialData?.tags?.map(t => t.name).join(", ") || ""
  );
  const [imageFile, setImageFile] = useState<File | undefined>();

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    const form = event.currentTarget;
    event.preventDefault();

    if (form.checkValidity() === false) {
      event.stopPropagation();
      setValidated(true);
      return;
    }

    const tagNames = tagsString.split(",").map(t => t.trim()).filter(t => t !== "");
    onSubmit({ ...formData, tagNames } as any, imageFile);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    const val = type === "number" ? parseFloat(value) : value;
    setFormData((prev) => ({ ...prev, [name]: val }));
  };

  // Dynamic Item Helpers
  const addArrayItem = (key: "learningPoints" | "prerequisites") => {
    setFormData((prev) => ({
      ...prev,
      [key]: [...(prev[key] || []), ""],
    }));
  };

  const updateArrayItem = (key: "learningPoints" | "prerequisites", index: number, value: string) => {
    setFormData((prev) => {
      const newArr = [...(prev[key] || [])];
      newArr[index] = value;
      return { ...prev, [key]: newArr };
    });
  };

  const removeArrayItem = (key: "learningPoints" | "prerequisites", index: number) => {
    setFormData((prev) => ({
      ...prev,
      [key]: (prev[key] || []).filter((_, i) => i !== index),
    }));
  };

  // Module/Lesson Helpers
  const addModule = () => {
    setFormData((prev) => ({
      ...prev,
      modules: [
        ...(prev.modules || []),
        { id: 0, title: "", description: "", orderIndex: (prev.modules?.length || 0) + 1, lessons: [] },
      ],
    }));
  };

  const updateModule = (index: number, field: keyof ModuleDTO, value: any) => {
    setFormData((prev) => {
      const newModules = [...(prev.modules || [])];
      newModules[index] = { ...newModules[index], [field]: value };
      return { ...prev, modules: newModules };
    });
  };

  const removeModule = (index: number) => {
    setFormData((prev) => ({
      ...prev,
      modules: (prev.modules || []).filter((_, i) => i !== index),
    }));
  };

  const addLesson = (moduleIndex: number) => {
    setFormData((prev) => {
      const newModules = [...(prev.modules || [])];
      const lessons = [...newModules[moduleIndex].lessons];
      lessons.push({
        id: 0,
        title: "",
        videoUrl: "",
        description: "",
        orderIndex: lessons.length + 1,
      });
      newModules[moduleIndex] = { ...newModules[moduleIndex], lessons };
      return { ...prev, modules: newModules };
    });
  };

  const updateLesson = (moduleIndex: number, lessonIndex: number, field: keyof LessonDTO, value: any) => {
    setFormData((prev) => {
      const newModules = [...(prev.modules || [])];
      const lessons = [...newModules[moduleIndex].lessons];
      lessons[lessonIndex] = { ...lessons[lessonIndex], [field]: value };
      newModules[moduleIndex] = { ...newModules[moduleIndex], lessons };
      return { ...prev, modules: newModules };
    });
  };

  const removeLesson = (moduleIndex: number, lessonIndex: number) => {
    setFormData((prev) => {
      const newModules = [...(prev.modules || [])];
      const lessons = newModules[moduleIndex].lessons.filter((_, i) => i !== lessonIndex);
      newModules[moduleIndex] = { ...newModules[moduleIndex], lessons };
      return { ...prev, modules: newModules };
    });
  };

  return (
    <Form noValidate validated={validated} onSubmit={handleSubmit} className="needs-validation">
      <Row className="g-4">
        <Col lg={8}>
          {/* Basic Info */}
          <Card className="shadow-sm border-0 mb-4 rounded-4">
            <Card.Header className="bg-white py-3 border-bottom-0">
              <h5 className="mb-0 fw-bold">Información Básica</h5>
            </Card.Header>
            <Card.Body className="pt-0">
              <Form.Group className="mb-3">
                <Form.Label className="fw-medium small text-muted">Imagen del Curso</Form.Label>
                <Form.Control
                  type="file"
                  accept="image/*"
                  className="rounded-3"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setImageFile(e.target.files?.[0])}
                />
                <Form.Text className="text-muted small">Se recomienda una imagen de 1280x720 px. Deja vacío para mantener la actual.</Form.Text>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label className="fw-medium small text-muted">Título del Curso</Form.Label>
                <Form.Control
                  required
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  placeholder="Ej: Introducción a React"
                  className="rounded-3 shadow-none border"
                />
                <Form.Control.Feedback type="invalid">Título de curso no válido.</Form.Control.Feedback>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label className="fw-medium small text-muted">Descripción Breve</Form.Label>
                <Form.Control
                  required
                  as="textarea"
                  rows={3}
                  name="shortDescription"
                  value={formData.shortDescription}
                  onChange={handleInputChange}
                  placeholder="Resumen corto del curso..."
                  className="rounded-3 shadow-none border"
                />
                <Form.Control.Feedback type="invalid">Descripción breve no válida.</Form.Control.Feedback>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label className="fw-medium small text-muted">Descripción Detallada</Form.Label>
                <Form.Control
                  required
                  as="textarea"
                  rows={6}
                  name="longDescription"
                  value={formData.longDescription}
                  onChange={handleInputChange}
                  placeholder="Detalles completos del curso..."
                  className="rounded-3 shadow-none border"
                />
                <Form.Control.Feedback type="invalid">Descripción detallada no válida.</Form.Control.Feedback>
              </Form.Group>

              <Row>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label className="fw-medium small text-muted">Idioma</Form.Label>
                    <Form.Select
                      name="language"
                      value={formData.language}
                      onChange={handleInputChange}
                      className="rounded-3 shadow-none border"
                      required
                    >
                      <option value="Español">Español</option>
                      <option value="Inglés">Inglés</option>
                      <option value="Francés">Francés</option>
                      <option value="Alemán">Alemán</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label className="fw-medium small text-muted">Estado</Form.Label>
                    <Form.Select
                      name="status"
                      value={formData.status}
                      onChange={handleInputChange}
                      className="rounded-3 shadow-none border"
                    >
                      <option value="DRAFT">Borrador</option>
                      <option value="PENDING_REVIEW">En revisión</option>
                      <option value="PUBLISHED">Publicado</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
              </Row>
            </Card.Body>
          </Card>

          {/* Learning Points */}
          <Card className="shadow-sm border-0 mb-4 rounded-4">
            <Card.Header className="bg-white py-3 border-bottom-0 d-flex justify-content-between align-items-center">
              <h5 className="mb-0 fw-bold">Lo que aprenderás</h5>
              <Button
                variant="outline-primary"
                size="sm"
                className="rounded-pill px-3"
                style={{ borderColor: "var(--accent-color)", color: "var(--accent-color)" }}
                onClick={() => addArrayItem("learningPoints")}
              >
                + Añadir
              </Button>
            </Card.Header>
            <Card.Body className="pt-0">
              {formData.learningPoints?.map((p, i) => (
                <InputGroup key={i} className="mb-2 shadow-sm rounded overflow-hidden">
                  <InputGroup.Text className="bg-light border-end-0"><i className="bi bi-check-lg" style={{ color: "var(--accent-color)" }}></i></InputGroup.Text>
                  <Form.Control
                    required
                    value={p}
                    onChange={(e) => updateArrayItem("learningPoints", i, e.target.value)}
                    placeholder="Ej: Dominar hooks de React"
                    className="border-start-0 shadow-none border"
                  />
                  <Button
                    variant="outline-secondary"
                    className="border-start-0 px-3 bg-light text-muted"
                    onClick={() => removeArrayItem("learningPoints", i)}
                  >×</Button>
                </InputGroup>
              ))}
            </Card.Body>
          </Card>

          {/* Prerequisites */}
          <Card className="shadow-sm border-0 mb-4 rounded-4">
            <Card.Header className="bg-white py-3 border-bottom-0 d-flex justify-content-between align-items-center">
              <h5 className="mb-0 fw-bold">Requisitos Previos</h5>
              <Button
                variant="outline-primary"
                size="sm"
                className="rounded-pill px-3"
                style={{ borderColor: "var(--accent-color)", color: "var(--accent-color)" }}
                onClick={() => addArrayItem("prerequisites")}
              >
                + Añadir
              </Button>
            </Card.Header>
            <Card.Body className="pt-0">
              {formData.prerequisites?.map((p, i) => (
                <InputGroup key={i} className="mb-2 shadow-sm rounded overflow-hidden">
                  <InputGroup.Text className="bg-light border-end-0"><i className="bi bi-info-circle text-muted"></i></InputGroup.Text>
                  <Form.Control
                    required
                    value={p}
                    onChange={(e) => updateArrayItem("prerequisites", i, e.target.value)}
                    placeholder="Ej: Conocimientos de HTML"
                    className="border-start-0 shadow-none border"
                  />
                  <Button
                    variant="outline-secondary"
                    className="border-start-0 px-3 bg-light text-muted"
                    onClick={() => removeArrayItem("prerequisites", i)}
                  >×</Button>
                </InputGroup>
              ))}
            </Card.Body>
          </Card>

          {/* Modules */}
          <Card className="shadow-sm border-0 mb-4 rounded-4 overflow-hidden">
            <Card.Header className="bg-white py-3 border-bottom-0 d-flex justify-content-between align-items-center">
              <h5 className="mb-0 fw-bold">Contenido del Curso</h5>
              <Button
                variant="success"
                size="sm"
                className="rounded-pill px-3 shadow-sm border-0"
                onClick={addModule}
              >
                + Añadir Módulo
              </Button>
            </Card.Header>
            <Card.Body className="pt-0">
              {formData.modules?.map((m, mIdx) => (
                <div key={mIdx} className="border rounded-4 p-4 mb-4 bg-light shadow-sm position-relative">
                  <div className="d-flex justify-content-between align-items-start mb-4">
                    <div className="flex-grow-1 me-3">
                      <Form.Control
                        required
                        className="fw-bold fs-5 mb-2 border-0 bg-transparent shadow-none p-0"
                        value={m.title}
                        onChange={(e) => updateModule(mIdx, "title", e.target.value)}
                        placeholder={`Título del Módulo ${mIdx + 1}`}
                      />
                      <hr className="my-1 opacity-25" />
                      <Form.Control
                        required
                        className="text-muted small border-0 bg-transparent shadow-none p-0"
                        value={m.description}
                        onChange={(e) => updateModule(mIdx, "description", e.target.value)}
                        placeholder="Breve descripción del módulo"
                      />
                    </div>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      className="rounded-pill px-3 border-0 bg-white shadow-sm"
                      onClick={() => removeModule(mIdx)}
                    >
                      Eliminar
                    </Button>
                  </div>

                  <div className="ms-3 ps-4 border-start position-relative">
                    <label className="fw-bold small text-muted text-uppercase mb-3 d-block" style={{ fontSize: "0.7rem", letterSpacing: "1px" }}>Lecciones</label>
                    {m.lessons.map((l, lIdx) => (
                      <div key={lIdx} className="input-group input-group-sm mb-3 shadow-sm rounded overflow-hidden">
                        <span className="input-group-text bg-white border-0"><i className="bi bi-play-circle text-primary opacity-50"></i></span>
                        <Form.Control
                          required
                          placeholder="Título de la lección"
                          value={l.title}
                          className="border-0 shadow-none"
                          onChange={(e) => updateLesson(mIdx, lIdx, "title", e.target.value)}
                        />
                        <Form.Control
                          required
                          placeholder="URL del vídeo"
                          value={l.videoUrl}
                          className="border-0 shadow-none bg-light"
                          onChange={(e) => updateLesson(mIdx, lIdx, "videoUrl", e.target.value)}
                        />
                        <Button
                          variant="outline-secondary"
                          className="border-0 px-3 bg-white text-muted"
                          onClick={() => removeLesson(mIdx, lIdx)}
                        >×</Button>
                      </div>
                    ))}
                    <Button
                      variant="link"
                      size="sm"
                      className="text-decoration-none p-0 fw-bold mt-2 d-flex align-items-center gap-1"
                      onClick={() => addLesson(mIdx)}
                    >
                      <i className="bi bi-plus-circle"></i> Añadir Lección
                    </Button>
                  </div>
                </div>
              ))}
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          {/* Sidebar Details */}
          <Card className="shadow-sm border-0 rounded-4 mb-4 sticky-top" style={{ top: "100px" }}>
            <Card.Header className="bg-white py-3 border-bottom-0">
              <h5 className="mb-0 fw-bold">Detalles Adicionales</h5>
            </Card.Header>
            <Card.Body className="pt-0">
              <Form.Group className="mb-3">
                <Form.Label className="fw-medium small text-muted">Precio (€)</Form.Label>
                <InputGroup className="shadow-sm rounded-3 overflow-hidden border">
                  <InputGroup.Text className="bg-white border-0">€</InputGroup.Text>
                  <Form.Control
                    required
                    type="number"
                    name="price"
                    step="0.01"
                    min="0"
                    value={formData.price}
                    onChange={handleInputChange}
                    className="border-0 shadow-none py-2"
                  />
                </InputGroup>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label className="fw-medium small text-muted">Horas de Vídeo</Form.Label>
                <Form.Control
                  required
                  type="number"
                  name="videoHours"
                  step="0.1"
                  min="0"
                  value={formData.videoHours}
                  onChange={handleInputChange}
                  className="rounded-3 shadow-none border py-2"
                />
              </Form.Group>

              <Form.Group className="mb-4">
                <Form.Label className="fw-medium small text-muted">Recursos descargables</Form.Label>
                <Form.Control
                  required
                  type="number"
                  name="downloadableResources"
                  min="0"
                  value={formData.downloadableResources}
                  onChange={handleInputChange}
                  className="rounded-3 shadow-none border py-2"
                />
              </Form.Group>

              <Form.Group className="mb-4">
                <Form.Label className="fw-medium small text-muted">Etiquetas (separadas por comas)</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Ej: IA, React, Programación"
                  value={tagsString}
                  onChange={(e) => setTagsString(e.target.value)}
                  className="rounded-3 shadow-none border py-2"
                />
                <Form.Text className="text-muted small">Ayudan a los usuarios a encontrar tu curso.</Form.Text>
              </Form.Group>

              <hr className="mb-4 opacity-10" />

              <div className="d-grid gap-3">
                <Button
                  variant="primary"
                  type="submit"
                  size="lg"
                  disabled={isSubmitting}
                  className="py-3 shadow-sm fw-bold rounded-3 border-0 transition-all"
                  style={{ background: "#d96d3c", transform: isSubmitting ? "none" : "scale(1)" }}
                >
                  {isSubmitting ? "Guardando..." : initialData?.id ? "Actualizar Curso" : "Publicar Curso"}
                </Button>
                <Button
                  variant="light"
                  onClick={() => window.history.back()}
                  className="py-2 text-muted fw-medium rounded-3 border bg-white"
                >
                  Cancelar
                </Button>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Form>
  );
}
