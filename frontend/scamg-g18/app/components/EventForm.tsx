import { useState } from "react";
import { Form, Button, Row, Col, InputGroup, Card } from "react-bootstrap";
import type { EventDTO } from "~/dtos/EventDTO";

interface EventFormProps {
  initialData?: Partial<EventDTO>;
  onSubmit: (data: EventDTO, imageFile?: File) => void;
  isSubmitting?: boolean;
}

export default function EventForm({ initialData, onSubmit, isSubmitting }: EventFormProps) {
  const [validated, setValidated] = useState(false);
  const [formData, setFormData] = useState<Partial<EventDTO>>({
    title: initialData?.title || "",
    description: initialData?.description || "",
    price: initialData?.price || 0,
    priceCents: initialData?.priceCents || 0,
    capacity: initialData?.capacity || 50,
    category: initialData?.category || "Networking",
    status: initialData?.status || "DRAFT",
    locationName: initialData?.locationName || "",
    locationAddress: initialData?.locationAddress || "",
    locationCity: initialData?.locationCity || "Madrid",
    locationCountry: initialData?.locationCountry || "España",
    startDateStr: initialData?.startDateStr || "",
    startTimeStr: initialData?.startTimeStr || "",
    endDateStr: initialData?.endDateStr || "",
    endTimeStr: initialData?.endTimeStr || "",
    speakerNames: initialData?.speakerNames || [""],
    sessionTitles: initialData?.sessionTitles || [""],
    sessionTimes: initialData?.sessionTimes || [""],
    sessionDescriptions: initialData?.sessionDescriptions || [""],
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

    // Combine date and time strings if needed or just send as-is for backend to parse
    onSubmit(formData as EventDTO, imageFile);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    const val = type === "number" ? parseFloat(value) : value;
    setFormData((prev) => ({ ...prev, [name]: val }));
  };

  const updateArrayItem = (key: "speakerNames" | "sessionTitles" | "sessionTimes" | "sessionDescriptions", index: number, value: string) => {
    setFormData((prev) => {
      const newArr = [...(prev[key] || [])];
      newArr[index] = value;
      return { ...prev, [key]: newArr };
    });
  };

  const addItem = () => {
    setFormData((prev) => ({
      ...prev,
      sessionTitles: [...(prev.sessionTitles || []), ""],
      sessionTimes: [...(prev.sessionTimes || []), ""],
      sessionDescriptions: [...(prev.sessionDescriptions || []), ""],
    }));
  };

  const removeItem = (index: number) => {
    setFormData((prev) => ({
      ...prev,
      sessionTitles: (prev.sessionTitles || []).filter((_, i) => i !== index),
      sessionTimes: (prev.sessionTimes || []).filter((_, i) => i !== index),
      sessionDescriptions: (prev.sessionDescriptions || []).filter((_, i) => i !== index),
    }));
  };

  return (
    <Form noValidate validated={validated} onSubmit={handleSubmit} className="p-4 bg-white rounded shadow-sm">
      <h3 className="mb-4">{initialData?.id ? "Editar Evento" : "Nuevo Evento"}</h3>

      <Row className="mb-3">
        <Form.Group as={Col} md="8" controlId="eventTitle">
          <Form.Label>Título del Evento</Form.Label>
          <Form.Control
            required
            type="text"
            name="title"
            value={formData.title}
            onChange={handleInputChange}
          />
        </Form.Group>
        <Form.Group as={Col} md="4" controlId="eventCategory">
          <Form.Label>Categoría</Form.Label>
          <Form.Control
            required
            type="text"
            name="category"
            value={formData.category}
            onChange={handleInputChange}
          />
        </Form.Group>
      </Row>

      <Form.Group className="mb-3" controlId="eventDescription">
        <Form.Label>Descripción del Evento</Form.Label>
        <Form.Control
          required
          as="textarea"
          rows={3}
          name="description"
          value={formData.description}
          onChange={handleInputChange}
        />
      </Form.Group>

      <Row className="mb-3">
        <Col md={3}>
           <Form.Group controlId="eventPrice">
             <Form.Label>Precio (€)</Form.Label>
             <Form.Control
               required
               type="number"
               name="price"
               value={formData.price}
               onChange={handleInputChange}
             />
           </Form.Group>
        </Col>
        <Col md={3}>
           <Form.Group controlId="eventCapacity">
             <Form.Label>Capacidad</Form.Label>
             <Form.Control
               required
               type="number"
               name="capacity"
               value={formData.capacity}
               onChange={handleInputChange}
             />
           </Form.Group>
        </Col>
        <Col md={6}>
           <Form.Group controlId="eventImage">
             <Form.Label>Imagen promocional</Form.Label>
             <Form.Control
               type="file"
               onChange={(e: React.ChangeEvent<HTMLInputElement>) => setImageFile(e.target.files?.[0])}
             />
           </Form.Group>
        </Col>
      </Row>

      <hr className="my-4" />

      <h5>Fecha y Hora</h5>
      <Row className="mb-3">
        <Col md={3}>
          <Form.Label>Fecha Inicio</Form.Label>
          <Form.Control required type="date" name="startDateStr" value={formData.startDateStr} onChange={handleInputChange} />
        </Col>
        <Col md={3}>
          <Form.Label>Hora Inicio</Form.Label>
          <Form.Control required type="time" name="startTimeStr" value={formData.startTimeStr} onChange={handleInputChange} />
        </Col>
        <Col md={3}>
          <Form.Label>Fecha Fin</Form.Label>
          <Form.Control required type="date" name="endDateStr" value={formData.endDateStr} onChange={handleInputChange} />
        </Col>
        <Col md={3}>
          <Form.Label>Hora Fin</Form.Label>
          <Form.Control required type="time" name="endTimeStr" value={formData.endTimeStr} onChange={handleInputChange} />
        </Col>
      </Row>

      <hr className="my-4" />

      <h5>Ubicación</h5>
      <Row className="mb-3">
        <Col md={6}>
          <Form.Label>Lugar / Nombre</Form.Label>
          <Form.Control required name="locationName" value={formData.locationName} onChange={handleInputChange} />
        </Col>
        <Col md={6}>
          <Form.Label>Dirección</Form.Label>
          <Form.Control required name="locationAddress" value={formData.locationAddress} onChange={handleInputChange} />
        </Col>
      </Row>
      <Row className="mb-3">
        <Col md={6}>
          <Form.Label>Ciudad</Form.Label>
          <Form.Control required name="locationCity" value={formData.locationCity} onChange={handleInputChange} />
        </Col>
        <Col md={6}>
          <Form.Label>País</Form.Label>
          <Form.Control required name="locationCountry" value={formData.locationCountry} onChange={handleInputChange} />
        </Col>
      </Row>

      <hr className="my-4" />

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h5>Agenda / Sesiones</h5>
        <Button variant="outline-success" size="sm" onClick={addItem}>+ Añadir Sesión</Button>
      </div>

      {formData.sessionTitles?.map((_, idx) => (
        <Card key={idx} className="mb-3 bg-light">
          <Card.Body>
            <div className="d-flex justify-content-between mb-2">
              <strong>Sesión {idx + 1}</strong>
              <Button variant="link" className="text-danger p-0" onClick={() => removeItem(idx)}>Eliminar</Button>
            </div>
            <Row className="g-2">
               <Col md={3}>
                 <Form.Control
                   required
                   type="time"
                   value={formData.sessionTimes?.[idx] || ""}
                   onChange={(e) => updateArrayItem("sessionTimes", idx, e.target.value)}
                 />
               </Col>
               <Col md={9}>
                 <Form.Control
                   required
                   placeholder="Título de la sesión"
                   value={formData.sessionTitles?.[idx] || ""}
                   onChange={(e) => updateArrayItem("sessionTitles", idx, e.target.value)}
                 />
               </Col>
               <Col md={12}>
                 <Form.Control
                   as="textarea"
                   rows={2}
                   placeholder="Descripción breve"
                   value={formData.sessionDescriptions?.[idx] || ""}
                   onChange={(e) => updateArrayItem("sessionDescriptions", idx, e.target.value)}
                 />
               </Col>
            </Row>
          </Card.Body>
        </Card>
      ))}

      <div className="d-grid mt-5">
        <Button variant="primary" type="submit" size="lg" disabled={isSubmitting}>
          {isSubmitting ? "Guardando..." : initialData?.id ? "Actualizar Evento" : "Crear Evento"}
        </Button>
      </div>
    </Form>
  );
}
