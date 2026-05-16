import { useState, useEffect, useRef } from "react";
import { Form, Button, Row, Col, InputGroup, Card, ListGroup } from "react-bootstrap";
import type { EventDTO } from "~/dtos/EventDTO";

// Helper para cargar scripts externos
async function loadExternalScript(src: string, id: string): Promise<void> {
  return new Promise((resolve) => {
    if (document.getElementById(id)) return resolve();
    const script = document.createElement("script");
    script.src = src;
    script.id = id;
    script.onload = () => resolve();
    document.head.appendChild(script);
  });
}

async function loadExternalStyle(href: string, id: string): Promise<void> {
  if (document.getElementById(id)) return;
  const link = document.createElement("link");
  link.rel = "stylesheet";
  link.href = href;
  link.id = id;
  document.head.appendChild(link);
}

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
    locationLatitude: initialData?.locationLatitude || 40.4168,
    locationLongitude: initialData?.locationLongitude || -3.7038,
    startDateStr: initialData?.startDateStr || "",
    startTimeStr: initialData?.startTimeStr || "",
    endDateStr: initialData?.endDateStr || "",
    endTimeStr: initialData?.endTimeStr || "",
    speakerNames: initialData?.speakerNames || [""],
    sessionTitles: initialData?.sessionTitles || [""],
    sessionTimes: initialData?.sessionTimes || [""],
    sessionDescriptions: initialData?.sessionDescriptions || [""],
  });
  const [touched, setTouched] = useState<Record<string, boolean>>({});
  const [imageFile, setImageFile] = useState<File | undefined>();

  const isFieldInvalid = (name: string) => {
    if (!touched[name]) return false;
    const value = (formData as any)[name];

    switch (name) {
      case "title":
        return !value || value.length < 3;
      case "description":
        return !value || value.length < 10;
      case "startDateStr":
      case "startTimeStr":
      case "endDateStr":
      case "endTimeStr":
      case "locationName":
      case 'locationAddress':
      case 'locationCity':
      case 'locationCountry':
        return !value;
      case "price":
        return value === undefined || value === null || isNaN(value) || value < 0;
      case "capacity":
        return value === undefined || value === null || isNaN(value) || value < 1;
      default:
        return false;
    }
  };

  const isFormValid = () => {
    return (
      (formData.title?.length || 0) >= 3 &&
      (formData.description?.length || 0) >= 10 &&
      formData.startDateStr &&
      formData.startTimeStr &&
      formData.endDateStr &&
      formData.endTimeStr &&
      formData.locationName &&
      formData.locationAddress &&
      formData.locationCity &&
      formData.locationCountry &&
      formData.price !== undefined &&
      !isNaN(formData.price as number) &&
      (formData.price as number) >= 0 &&
      formData.capacity !== undefined &&
      !isNaN(formData.capacity as number) &&
      (formData.capacity as number) >= 1
    );
  };
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const mapRef = useRef<any>(null);
  const markerRef = useRef<any>(null);
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const searchTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    async function initLeaflet() {
      await loadExternalStyle("https://unpkg.com/leaflet@1.9.4/dist/leaflet.css", "leaflet-css");
      await loadExternalScript("https://unpkg.com/leaflet@1.9.4/dist/leaflet.js", "leaflet-js");
      
      const L = (window as any).L;
      if (!L || !mapContainerRef.current) return;

      if (!mapRef.current) {
        mapRef.current = L.map(mapContainerRef.current).setView([formData.locationLatitude || 40.4168, formData.locationLongitude || -3.7038], 15);
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
          attribution: "&copy; OpenStreetMap contributors",
        }).addTo(mapRef.current);
        markerRef.current = L.marker([formData.locationLatitude || 40.4168, formData.locationLongitude || -3.7038]).addTo(mapRef.current);
      }
    }
    initLeaflet();
  }, []);

  const updateMap = (lat: number, lon: number) => {
    const L = (window as any).L;
    if (mapRef.current && L) {
      const pos = [lat, lon];
      mapRef.current.setView(pos, 15);
      if (markerRef.current) {
        markerRef.current.setLatLng(pos);
      } else {
        markerRef.current = L.marker(pos).addTo(mapRef.current);
      }
    }
  };

  const handleLocationSearch = (query: string) => {
    setFormData(prev => ({ ...prev, locationName: query }));
    
    if (searchTimeoutRef.current) clearTimeout(searchTimeoutRef.current);
    
    if (query.length < 3) {
      setSearchResults([]);
      return;
    }

    searchTimeoutRef.current = setTimeout(async () => {
      setIsSearching(true);
      try {
        const res = await fetch(`/api/v1/events/locations?q=${encodeURIComponent(query)}`);
        if (res.ok) {
          const data = await res.json();
          setSearchResults(data);
        }
      } catch (err) {
        console.error("Error searching location:", err);
      } finally {
        setIsSearching(false);
      }
    }, 500);
  };

  const selectLocation = (result: any) => {
    const address = result.address || {};
    const newLat = parseFloat(result.lat);
    const newLon = parseFloat(result.lon);
    
    setFormData(prev => ({
      ...prev,
      locationName: result.display_name.split(",")[0],
      locationAddress: address.road || address.pedestrian || result.display_name,
      locationCity: address.city || address.town || address.village || "",
      locationCountry: address.country || "",
      locationLatitude: newLat,
      locationLongitude: newLon,
    }));
    
    setSearchResults([]);
    updateMap(newLat, newLon);
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    const form = event.currentTarget;
    event.preventDefault();
    
    if (form.checkValidity() === false || !isFormValid()) {
      event.stopPropagation();
      setValidated(true);
      
      // Mark all visible fields as touched to show errors
      const allTouched: Record<string, boolean> = { ...touched };
      ['title', 'description', 'startDateStr', 'startTimeStr', 'endDateStr', 'endTimeStr', 'locationName', 'locationAddress', 'locationCity', 'locationCountry', 'price', 'capacity'].forEach(field => {
        allTouched[field] = true;
      });
      setTouched(allTouched);
      return;
    }

    // Combine date and time strings if needed or just send as-is for backend to parse
    onSubmit(formData as EventDTO, imageFile);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    const val = type === "number" ? parseFloat(value) : value;
    setFormData((prev) => ({ ...prev, [name]: val }));
    setTouched((prev) => ({ ...prev, [name]: true }));
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
    <Form noValidate validated={validated} onSubmit={handleSubmit} className="needs-validation">
      <Row className="g-4">
        {/* Main Column */}
        <Col lg={8}>
          {/* Basic Info */}
          <Card className="shadow-sm border-0 mb-4 rounded-4">
            <Card.Header className="bg-white py-3 border-bottom-0">
              <h5 className="mb-0 fw-bold">Información Básica</h5>
            </Card.Header>
            <Card.Body className="pt-0">
              <Form.Group className="mb-3">
                <Form.Label className="fw-medium small text-muted">Imagen del Evento</Form.Label>
                <Form.Control
                  type="file"
                  accept="image/*"
                  className="rounded-3"
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setImageFile(e.target.files?.[0])}
                />
                <Form.Text className="text-muted small">Se recomienda una imagen de 1280x720 px.</Form.Text>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label className="fw-medium small text-muted">Título del Evento</Form.Label>
                <Form.Control
                  required
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  placeholder="Ej: Foro de Innovación 2026"
                  className={`rounded-3 shadow-none border ${isFieldInvalid('title') ? 'is-invalid' : touched.title ? 'is-valid' : ''}`}
                />
                <Form.Control.Feedback type="invalid">El título debe tener al menos 3 caracteres.</Form.Control.Feedback>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label className="fw-medium small text-muted">Descripción Detallada</Form.Label>
                <Form.Control
                  required
                  as="textarea"
                  rows={6}
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  placeholder="Detalles completos del evento..."
                  className={`rounded-3 shadow-none border ${isFieldInvalid('description') ? 'is-invalid' : touched.description ? 'is-valid' : ''}`}
                />
                <Form.Control.Feedback type="invalid">La descripción debe tener al menos 10 caracteres.</Form.Control.Feedback>
              </Form.Group>

              <Row>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label className="fw-medium small text-muted">Fecha de Inicio</Form.Label>
                    <Form.Control 
                      required 
                      type="date" 
                      name="startDateStr" 
                      value={formData.startDateStr} 
                      onChange={handleInputChange} 
                      className={`rounded-3 shadow-none border ${isFieldInvalid('startDateStr') ? 'is-invalid' : touched.startDateStr ? 'is-valid' : ''}`}
                    />
                    <Form.Control.Feedback type="invalid">Fecha de inicio obligatoria.</Form.Control.Feedback>
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label className="fw-medium small text-muted">Hora de Inicio</Form.Label>
                    <Form.Control 
                      required 
                      type="time" 
                      name="startTimeStr" 
                      value={formData.startTimeStr} 
                      onChange={handleInputChange} 
                      className={`rounded-3 shadow-none border ${isFieldInvalid('startTimeStr') ? 'is-invalid' : touched.startTimeStr ? 'is-valid' : ''}`}
                    />
                    <Form.Control.Feedback type="invalid">Hora de inicio obligatoria.</Form.Control.Feedback>
                  </Form.Group>
                </Col>
              </Row>
              <Row>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label className="fw-medium small text-muted">Fecha de Fin</Form.Label>
                    <Form.Control 
                      required 
                      type="date" 
                      name="endDateStr" 
                      value={formData.endDateStr} 
                      onChange={handleInputChange} 
                      className={`rounded-3 shadow-none border ${isFieldInvalid('endDateStr') ? 'is-invalid' : touched.endDateStr ? 'is-valid' : ''}`}
                    />
                    <Form.Control.Feedback type="invalid">Fecha de fin obligatoria.</Form.Control.Feedback>
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label className="fw-medium small text-muted">Hora de Fin</Form.Label>
                    <Form.Control 
                      required 
                      type="time" 
                      name="endTimeStr" 
                      value={formData.endTimeStr} 
                      onChange={handleInputChange} 
                      className={`rounded-3 shadow-none border ${isFieldInvalid('endTimeStr') ? 'is-invalid' : touched.endTimeStr ? 'is-valid' : ''}`}
                    />
                    <Form.Control.Feedback type="invalid">Hora de fin obligatoria.</Form.Control.Feedback>
                  </Form.Group>
                </Col>
              </Row>
            </Card.Body>
          </Card>

          {/* Location Info */}
          <Card className="shadow-sm border-0 mb-4 rounded-4">
            <Card.Header className="bg-white py-3 border-bottom-0">
              <h5 className="mb-0 fw-bold">Ubicación</h5>
            </Card.Header>
            <Card.Body className="pt-0">
               <Form.Group className="mb-3 position-relative">
                  <Form.Label className="fw-medium small text-muted">Lugar / Nombre</Form.Label>
                  <InputGroup className="shadow-sm rounded-3 overflow-hidden border">
                    <InputGroup.Text className="bg-white border-0"><i className="bi bi-geo-alt text-muted"></i></InputGroup.Text>
                    <Form.Control 
                        required 
                        name="locationName" 
                        value={formData.locationName} 
                        onChange={(e) => handleLocationSearch(e.target.value)} 
                        placeholder="Nombre del lugar"
                        className={`border-0 shadow-none py-2 ${isFieldInvalid('locationName') ? 'is-invalid' : touched.locationName ? 'is-valid' : ''}`}
                        autoComplete="off"
                    />
                    <Form.Control.Feedback type="invalid">El nombre de la ubicación es obligatorio.</Form.Control.Feedback>
                  </InputGroup>
                  
                  {searchResults.length > 0 && (
                    <ListGroup className="position-absolute w-100 shadow-lg z-3 mt-1" style={{ maxHeight: "200px", overflowY: "auto" }}>
                      {searchResults.map((res, idx) => (
                        <ListGroup.Item 
                          key={idx} 
                          action 
                          onClick={() => selectLocation(res)}
                          className="small py-2 border-0 border-bottom"
                        >
                          {res.display_name}
                        </ListGroup.Item>
                      ))}
                    </ListGroup>
                  )}
                  {isSearching && (
                    <div className="position-absolute end-0 top-50 translate-middle-y me-3 z-3">
                      <div className="spinner-border spinner-border-sm" style={{ color: "var(--accent-color)" }} role="status"></div>
                    </div>
                  )}
               </Form.Group>

               <Row className="g-3">
                 <Col lg={5}>
                    <Form.Group className="mb-3">
                        <Form.Label className="fw-medium small text-muted">Dirección Completa</Form.Label>
                        <Form.Control 
                          required 
                          name="locationAddress" 
                          value={formData.locationAddress} 
                          onChange={handleInputChange} 
                          placeholder="Calle, número, etc."
                          className={`rounded-3 shadow-none border ${isFieldInvalid('locationAddress') ? 'is-invalid' : touched.locationAddress ? 'is-valid' : ''}`}
                        />
                        <Form.Control.Feedback type="invalid">La dirección es obligatoria.</Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label className="fw-medium small text-muted">Ciudad</Form.Label>
                        <Form.Control 
                            required 
                            name="locationCity" 
                            value={formData.locationCity} 
                            onChange={handleInputChange} 
                            placeholder="Madrid"
                            className={`rounded-3 shadow-none border ${isFieldInvalid('locationCity') ? 'is-invalid' : touched.locationCity ? 'is-valid' : ''}`}
                        />
                        <Form.Control.Feedback type="invalid">La ciudad es obligatoria.</Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label className="fw-medium small text-muted">País</Form.Label>
                        <Form.Control 
                            required 
                            name="locationCountry" 
                            value={formData.locationCountry} 
                            onChange={handleInputChange} 
                            placeholder="España"
                            className={`rounded-3 shadow-none border ${isFieldInvalid('locationCountry') ? 'is-invalid' : touched.locationCountry ? 'is-valid' : ''}`}
                        />
                        <Form.Control.Feedback type="invalid">El país es obligatorio.</Form.Control.Feedback>
                    </Form.Group>
                 </Col>
                 <Col lg={7}>
                    <div className="rounded-3 overflow-hidden border shadow-sm h-100" style={{ minHeight: "220px" }}>
                       <div ref={mapContainerRef} style={{ height: "100%", width: "100%", minHeight: "220px" }}></div>
                    </div>
                 </Col>
               </Row>
            </Card.Body>
          </Card>

          {/* Agenda */}
          <Card className="shadow-sm border-0 mb-4 rounded-4 overflow-hidden">
            <Card.Header className="bg-white py-3 border-bottom-0 d-flex justify-content-between align-items-center">
              <h5 className="mb-0 fw-bold">Agenda</h5>
              <Button 
                variant="outline-primary" 
                size="sm" 
                className="rounded-pill px-3"
                style={{ borderColor: "var(--accent-color)", color: "var(--accent-color)" }}
                type="button"
                onClick={addItem}
              >
                + Añadir Sesión
              </Button>
            </Card.Header>
            <Card.Body className="pt-0">
               {formData.sessionTitles?.map((_, idx) => (
                  <div key={idx} className="border rounded-4 p-3 mb-3 bg-light shadow-sm">
                     <div className="d-flex justify-content-between mb-3 align-items-center">
                        <span className="fw-bold text-muted small text-uppercase">Sesión {idx + 1}</span>
                        <Button 
                          variant="outline-danger" 
                          size="sm" 
                          className="border-0 bg-transparent text-danger p-0"
                          type="button"
                          onClick={() => removeItem(idx)}
                        >
                          Eliminar
                        </Button>
                     </div>
                     <Row className="g-3">
                        <Col md={3}>
                           <Form.Control
                              required
                              type="time"
                              className="rounded-3 border shadow-none"
                              value={formData.sessionTimes?.[idx] || ""}
                              onChange={(e) => updateArrayItem("sessionTimes", idx, e.target.value)}
                           />
                        </Col>
                        <Col md={9}>
                           <Form.Control
                              required
                              placeholder="Título de la sesión"
                              className="rounded-3 border shadow-none"
                              value={formData.sessionTitles?.[idx] || ""}
                              onChange={(e) => updateArrayItem("sessionTitles", idx, e.target.value)}
                           />
                        </Col>
                        <Col md={12}>
                           <Form.Control
                              as="textarea"
                              rows={2}
                              placeholder="Descripción breve de la sesión"
                              className="rounded-3 border shadow-none"
                              value={formData.sessionDescriptions?.[idx] || ""}
                              onChange={(e) => updateArrayItem("sessionDescriptions", idx, e.target.value)}
                           />
                        </Col>
                     </Row>
                  </div>
               ))}
            </Card.Body>
          </Card>
        </Col>

        {/* Sidebar Column */}
        <Col lg={4}>
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
                          className={`border-0 shadow-none py-2 ${isFieldInvalid('price') ? 'is-invalid' : touched.price ? 'is-valid' : ''}`}
                       />
                       <Form.Control.Feedback type="invalid">El precio no puede ser negativo.</Form.Control.Feedback>
                    </InputGroup>
                 </Form.Group>

                 <Form.Group className="mb-4">
                    <Form.Label className="fw-medium small text-muted">Capacidad (Personas)</Form.Label>
                    <Form.Control
                       required
                       type="number"
                       name="capacity"
                       min="1"
                       value={formData.capacity}
                       onChange={handleInputChange}
                       className={`rounded-3 shadow-none border py-2 ${isFieldInvalid('capacity') ? 'is-invalid' : touched.capacity ? 'is-valid' : ''}`}
                    />
                    <Form.Control.Feedback type="invalid">La capacidad debe ser al menos 1.</Form.Control.Feedback>
                 </Form.Group>

                 <Form.Group className="mb-4">
                    <Form.Label className="fw-medium small text-muted">Tipo de Evento</Form.Label>
                    <Form.Select 
                      name="category" 
                      value={formData.category} 
                      onChange={handleInputChange} 
                      className="rounded-3 shadow-none border py-2"
                    >
                      <option value="Conferencia">Conferencia</option>
                      <option value="Webinar">Webinar</option>
                      <option value="Taller">Taller</option>
                      <option value="Networking">Networking</option>
                    </Form.Select>
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
                      {isSubmitting ? "Publicando..." : initialData?.id ? "Actualizar Evento" : "Publicar Evento"}
                    </Button>
                    <Button 
                      variant="light" 
                      type="button"
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
