import { useState } from "react";
import { Form, Button, Row, Col, InputGroup, Card } from "react-bootstrap";
import type { CourseDTO, ModuleDTO, LessonDTO } from "~/dtos/CourseDTO";

interface CourseFormProps {
  initialData?: Partial<CourseDTO>;
  onSubmit: (data: CourseDTO, imageFile?: File) => void;
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
  const [imageFile, setImageFile] = useState<File | undefined>();

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    const form = event.currentTarget;
    event.preventDefault();
    
    if (form.checkValidity() === false) {
      event.stopPropagation();
      setValidated(true);
      return;
    }

    onSubmit(formData as CourseDTO, imageFile);
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
    <Form noValidate validated={validated} onSubmit={handleSubmit} className="p-4 bg-white rounded shadow-sm">
      <h3 className="mb-4">{initialData?.id ? "Editar Curso" : "Nuevo Curso"}</h3>
      
      <Row className="mb-3">
        <Form.Group as={Col} md="8" controlId="courseTitle">
          <Form.Label>Título del Curso</Form.Label>
          <Form.Control
            required
            type="text"
            name="title"
            value={formData.title}
            onChange={handleInputChange}
            placeholder="Ej: Emprendimiento Pro"
          />
          <Form.Control.Feedback type="invalid">Por favor, introduce un título.</Form.Control.Feedback>
        </Form.Group>

        <Form.Group as={Col} md="4" controlId="courseStatus">
          <Form.Label>Estado</Form.Label>
          <Form.Select name="status" value={formData.status} onChange={handleInputChange}>
            <option value="DRAFT">Borrador</option>
            <option value="PENDING_REVIEW">En revisión</option>
            <option value="PUBLISHED">Publicado</option>
            <option value="ARCHIVED">Archivado</option>
          </Form.Select>
        </Form.Group>
      </Row>

      <Form.Group className="mb-3" controlId="shortDescription">
        <Form.Label>Descripción Corta</Form.Label>
        <Form.Control
          required
          as="textarea"
          rows={2}
          name="shortDescription"
          value={formData.shortDescription}
          onChange={handleInputChange}
          placeholder="Un resumen rápido..."
        />
      </Form.Group>

      <Form.Group className="mb-3" controlId="longDescription">
        <Form.Label>Descripción Detallada</Form.Label>
        <Form.Control
          required
          as="textarea"
          rows={5}
          name="longDescription"
          value={formData.longDescription}
          onChange={handleInputChange}
          placeholder="Todo lo que el alumno necesita saber..."
        />
      </Form.Group>

      <Row className="mb-4">
        <Col md={3}>
          <Form.Group controlId="price">
            <Form.Label>Precio (Euros)</Form.Label>
            <InputGroup>
              <Form.Control
                required
                type="number"
                name="price"
                value={formData.price}
                onChange={handleInputChange}
                min="0"
              />
              <InputGroup.Text>€</InputGroup.Text>
            </InputGroup>
          </Form.Group>
        </Col>
        <Col md={3}>
          <Form.Group controlId="language">
            <Form.Label>Idioma</Form.Label>
            <Form.Control
              required
              type="text"
              name="language"
              value={formData.language}
              onChange={handleInputChange}
            />
          </Form.Group>
        </Col>
        <Col md={6}>
          <Form.Group controlId="image">
            <Form.Label>Imagen del Curso</Form.Label>
            <Form.Control
              type="file"
              accept="image/*"
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => setImageFile(e.target.files?.[0])}
            />
            <Form.Text className="text-muted">Deja vacío para mantener la imagen actual.</Form.Text>
          </Form.Group>
        </Col>
      </Row>

      <hr className="my-4" />

      {/* Dynamic Arrays */}
      <Row className="mb-4">
        <Col md={6}>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <h5>Lo que aprenderás</h5>
            <Button variant="outline-primary" size="sm" onClick={() => addArrayItem("learningPoints")}>+</Button>
          </div>
          {formData.learningPoints?.map((p, i) => (
            <InputGroup key={i} className="mb-2">
              <Form.Control
                required
                value={p}
                onChange={(e) => updateArrayItem("learningPoints", i, e.target.value)}
              />
              <Button variant="outline-danger" onClick={() => removeArrayItem("learningPoints", i)}>−</Button>
            </InputGroup>
          ))}
        </Col>
        <Col md={6}>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <h5>Requisitos</h5>
            <Button variant="outline-primary" size="sm" onClick={() => addArrayItem("prerequisites")}>+</Button>
          </div>
          {formData.prerequisites?.map((p, i) => (
            <InputGroup key={i} className="mb-2">
              <Form.Control
                required
                value={p}
                onChange={(e) => updateArrayItem("prerequisites", i, e.target.value)}
              />
              <Button variant="outline-danger" onClick={() => removeArrayItem("prerequisites", i)}>−</Button>
            </InputGroup>
          ))}
        </Col>
      </Row>

      <hr className="my-4" />

      {/* Modules and Lessons */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4>Módulos y Lecciones</h4>
        <Button variant="success" onClick={addModule}>+ Añadir Módulo</Button>
      </div>

      {formData.modules?.map((m, mIdx) => (
        <Card key={mIdx} className="mb-4 border-primary">
          <Card.Header className="bg-primary text-white d-flex justify-content-between align-items-center">
            <span>Módulo {mIdx + 1}</span>
            <Button variant="light" size="sm" onClick={() => removeModule(mIdx)}>Eliminar Módulo</Button>
          </Card.Header>
          <Card.Body>
            <Form.Group className="mb-3">
              <Form.Label>Título del Módulo</Form.Label>
              <Form.Control
                required
                value={m.title}
                onChange={(e) => updateModule(mIdx, "title", e.target.value)}
              />
            </Form.Group>
            
            <div className="ms-4">
              <h6>Lecciones</h6>
              {m.lessons.map((l, lIdx) => (
                <div key={lIdx} className="border-start ps-3 mb-3">
                   <Row className="g-2">
                     <Col md={5}>
                        <Form.Control
                          required
                          placeholder="Título de la lección"
                          value={l.title}
                          onChange={(e) => updateLesson(mIdx, lIdx, "title", e.target.value)}
                        />
                     </Col>
                     <Col md={6}>
                        <Form.Control
                          required
                          placeholder="URL del vídeo"
                          value={l.videoUrl}
                          onChange={(e) => updateLesson(mIdx, lIdx, "videoUrl", e.target.value)}
                        />
                     </Col>
                     <Col md={1}>
                        <Button variant="outline-danger" className="w-100" onClick={() => removeLesson(mIdx, lIdx)}>×</Button>
                     </Col>
                   </Row>
                </div>
              ))}
              <Button variant="outline-secondary" size="sm" onClick={() => addLesson(mIdx)}>+ Añadir Lección</Button>
            </div>
          </Card.Body>
        </Card>
      ))}

      <div className="d-grid mt-5">
        <Button variant="primary" type="submit" size="lg" disabled={isSubmitting}>
          {isSubmitting ? "Guardando..." : initialData?.id ? "Actualizar Curso" : "Crear Curso"}
        </Button>
      </div>
    </Form>
  );
}
