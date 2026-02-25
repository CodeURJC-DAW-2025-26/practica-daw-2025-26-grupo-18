package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Enrollment;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.EventRegistration;
import es.codeurjc.scam_g18.model.EventSession;
import es.codeurjc.scam_g18.model.Lesson;
import es.codeurjc.scam_g18.model.LessonProgress;
import es.codeurjc.scam_g18.model.Location;
import es.codeurjc.scam_g18.model.Module;
import es.codeurjc.scam_g18.model.Order;
import es.codeurjc.scam_g18.model.OrderItem;
import es.codeurjc.scam_g18.model.OrderStatus;
import es.codeurjc.scam_g18.model.Review;
import es.codeurjc.scam_g18.model.Role;
import es.codeurjc.scam_g18.model.Status;
import es.codeurjc.scam_g18.model.Subscription;
import es.codeurjc.scam_g18.model.SubscriptionStatus;
import es.codeurjc.scam_g18.model.Tag;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.CourseRepository;
import es.codeurjc.scam_g18.repository.EnrollmentRepository;
import es.codeurjc.scam_g18.repository.EventRegistrationRepository;
import es.codeurjc.scam_g18.repository.EventRepository;
import es.codeurjc.scam_g18.repository.LessonProgressRepository;
import es.codeurjc.scam_g18.repository.LocationRepository;
import es.codeurjc.scam_g18.repository.OrderItemRepository;
import es.codeurjc.scam_g18.repository.OrderRepository;
import es.codeurjc.scam_g18.repository.ReviewRepository;
import es.codeurjc.scam_g18.repository.RoleRepository;
import es.codeurjc.scam_g18.repository.SubscriptionRepository;
import es.codeurjc.scam_g18.repository.TagRepository;
import es.codeurjc.scam_g18.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DatabaseInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (!shouldSeed()) {
            return;
        }

        initializeRoles();

        SeedContext context = new SeedContext();
        context.users = initializeUsers();
        context.tags = initializeTags();
        context.locations = initializeLocations();

        List<Course> courses = initializeCourses(context);
        List<Event> events = initializeEvents(context);

        initializeEnrollmentsAndLessonProgress(context, courses);
        initializeReviews(context, courses);
        initializeEventRegistrations(context, events);
        initializeSubscriptions(context);
        initializeOrders(context, courses, events);

        refreshCourseSubscribers(courses);
        refreshEventAttendees(events);
    }

    private boolean shouldSeed() {
        return userRepository.count() == 0
                && courseRepository.count() == 0
                && eventRepository.count() == 0
                && orderRepository.count() == 0
                && subscriptionRepository.count() == 0;
    }

    private void initializeRoles() {
        ensureRoleExists("USER");
        ensureRoleExists("ADMIN");
        ensureRoleExists("SUBSCRIBED");
    }

    private Role ensureRoleExists(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    private Map<String, User> initializeUsers() {
        Map<String, User> users = new HashMap<>();

        Role userRole = ensureRoleExists("USER");
        Role adminRole = ensureRoleExists("ADMIN");
        Role subscribedRole = ensureRoleExists("SUBSCRIBED");

        users.put("admin", createUser("admin", "admin@scam.com", "adminpass", "MALE",
                LocalDate.of(1990, 1, 1), "Spain",
                Set.of(userRole, adminRole),
                "Administrador principal de la plataforma", "Supervisar calidad de contenido"));

        users.put("content_lead", createUser("content_lead", "lead@scam.com", "leadpass", "FEMALE",
                LocalDate.of(1992, 4, 17), "Spain",
                Set.of(userRole, adminRole),
                "Responsable de contenido premium", "Escalar catálogo educativo"));

        users.put("mentor_ai", createUser("mentor_ai", "mentor.ai@scam.com", "mentorpass", "MALE",
                LocalDate.of(1988, 9, 3), "Mexico",
                Set.of(userRole),
                "Creador experto en IA aplicada", "Ayudar a lanzar productos IA"));

        users.put("coach_growth", createUser("coach_growth", "coach.growth@scam.com", "coachpass", "FEMALE",
                LocalDate.of(1991, 11, 20), "Colombia",
                Set.of(userRole),
                "Coach de crecimiento profesional", "Optimizar hábitos de alto rendimiento"));

        users.put("finance_master", createUser("finance_master", "finance.master@scam.com", "financepass", "MALE",
                LocalDate.of(1987, 6, 9), "Argentina",
                Set.of(userRole),
                "Analista financiero y formador", "Impulsar alfabetización financiera"));

        for (int i = 1; i <= 18; i++) {
            String username = "learner" + i;
            String email = "learner" + i + "@scam.com";
            String gender = (i % 3 == 0) ? "PREFER_NOT_TO_SAY" : (i % 2 == 0 ? "FEMALE" : "MALE");
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            if (i <= 10) {
                roles.add(subscribedRole);
            }

            users.put(username, createUser(
                    username,
                    email,
                    "pass" + i,
                    gender,
                    LocalDate.of(1994 + (i % 8), (i % 12) + 1, (i % 27) + 1),
                    switch (i % 6) {
                        case 0 -> "Spain";
                        case 1 -> "Mexico";
                        case 2 -> "Chile";
                        case 3 -> "Peru";
                        case 4 -> "Colombia";
                        default -> "Argentina";
                    },
                    roles,
                    "Usuario activo de pruebas #" + i,
                    "Completar rutas formativas y asistir a eventos"));
        }

        return users;
    }

    private User createUser(String username, String email, String rawPassword, String gender, LocalDate birthDate,
            String country, Set<Role> roles, String shortDescription, String currentGoal) {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setGender(gender);
        user.setBirthDate(birthDate);
        user.setCountry(country);
        user.setShortDescription(shortDescription);
        user.setCurrentGoal(currentGoal);
        user.setWeeklyRoutine("3 sesiones semanales de aprendizaje y práctica");
        user.setComunity("Comunidad global de aprendizaje digital");
        user.setIsActive(true);
        user.setRoles(new HashSet<>(roles));
        return userRepository.save(user);
    }

    private Map<String, Tag> initializeTags() {
        String[] tagNames = {
                "Desarrollo Personal", "Emprendimiento", "Finanzas", "Liderazgo", "Productividad",
                "Libertad Financiera", "Ventas", "Marketing Digital", "IA", "Programación",
                "Comunicación", "Negociación", "Gestión del Tiempo", "Startups", "Networking", "Inversión"
        };

        Map<String, Tag> tags = new HashMap<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));
            tags.put(tagName, tag);
        }

        return tags;
    }

    private List<Location> initializeLocations() {
        List<Location> locations = new ArrayList<>();

        locations.add(createLocation("Palacio de Congresos", "Paseo de la Castellana 123", "Madrid", "Spain",
                40.45798, -3.69058));
        locations.add(createLocation("Tech Hub BCN", "Carrer de la Marina 15", "Barcelona", "Spain",
                41.39111, 2.19123));
        locations.add(createLocation("Innovation Center", "Gran Via 77", "Valencia", "Spain",
                39.46990, -0.37629));
        locations.add(createLocation("Campus Latam", "Av. Reforma 210", "Ciudad de Mexico", "Mexico",
                19.43307, -99.15394));
        locations.add(createLocation("Centro de Negocios Andino", "Cra 7 #71", "Bogota", "Colombia",
                4.65333, -74.08365));
        locations.add(createLocation("Distrito Creativo", "Av. Libertador 1450", "Buenos Aires", "Argentina",
                -34.58610, -58.40255));
        locations.add(createLocation("Foro Pacífico", "Av. Providencia 99", "Santiago", "Chile",
                -33.43212, -70.61672));
        locations.add(createLocation("Hub Innova", "Jr. de la Union 540", "Lima", "Peru",
                -12.04637, -77.04279));

        return locations;
    }

    private Location createLocation(String name, String address, String city, String country, Double latitude,
            Double longitude) {
        Location location = new Location();
        location.setName(name);
        location.setAddress(address);
        location.setCity(city);
        location.setCountry(country);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return locationRepository.save(location);
    }

    private List<Course> initializeCourses(SeedContext context) {
        List<User> creators = List.of(
                context.users.get("mentor_ai"),
                context.users.get("coach_growth"),
                context.users.get("finance_master"),
                context.users.get("admin"),
                context.users.get("content_lead"));

        String[] titles = {
                "Fundamentos de Libertad Financiera", "Emprender con IA desde Cero", "Liderazgo para Equipos Remotos",
                "Ventas Consultivas de Alto Ticket", "Productividad Extrema para Profesionales",
                "Inversión Inteligente en Mercados Globales", "Storytelling para Vender Ideas",
                "Marketing Digital 360", "Negociación Estratégica en Negocios", "Creación de Startups Rentables",
                "Automatización de Procesos con IA", "Finanzas Personales para Autónomos",
                "Comunicación Persuasiva para Líderes", "Escala tu Negocio de Servicios", "Gestión del Tiempo sin Estrés",
                "Diseño de Ofertas Irresistibles", "Networking Profesional Efectivo", "Roadmap de Carrera Tech"
        };

        String[][] tagMatrix = {
                { "Finanzas", "Libertad Financiera", "Inversión" },
                { "IA", "Emprendimiento", "Startups" },
                { "Liderazgo", "Comunicación", "Productividad" },
                { "Ventas", "Negociación", "Comunicación" },
                { "Productividad", "Gestión del Tiempo", "Desarrollo Personal" },
                { "Finanzas", "Inversión", "Liderazgo" },
                { "Comunicación", "Ventas", "Marketing Digital" },
                { "Marketing Digital", "Emprendimiento", "Networking" },
                { "Negociación", "Liderazgo", "Ventas" },
                { "Startups", "Emprendimiento", "Finanzas" },
                { "IA", "Programación", "Productividad" },
                { "Finanzas", "Gestión del Tiempo", "Desarrollo Personal" },
                { "Comunicación", "Liderazgo", "Desarrollo Personal" },
                { "Emprendimiento", "Ventas", "Productividad" },
                { "Gestión del Tiempo", "Productividad", "Desarrollo Personal" },
                { "Ventas", "Marketing Digital", "Negociación" },
                { "Networking", "Comunicación", "Liderazgo" },
                { "Programación", "IA", "Carrera" }
        };

        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            User creator = creators.get(i % creators.size());
            Set<Tag> courseTags = toTagSet(context.tags, tagMatrix[i]);
            List<Module> modules = buildModulesForCourse(i + 1);

            Status status;
            if (i < 13) {
                status = Status.PUBLISHED;
            } else if (i < 16) {
                status = Status.PENDING_REVIEW;
            } else {
                status = Status.DRAFT;
            }

            List<String> learningPoints = List.of(
                    "Aplicar un framework práctico desde la primera semana",
                    "Implementar un sistema medible con KPIs reales",
                    "Evitar errores frecuentes con plantillas reutilizables");

            List<String> prerequisites = List.of(
                    "Interés por la temática y compromiso de estudio",
                    "Acceso a ordenador y conexión estable a internet");

            Course course = new Course(
                    creator,
                    titles[i],
                    "Curso práctico para dominar " + titles[i].toLowerCase() + ".",
                    "Programa completo con enfoque aplicado y orientado a resultados para "
                            + titles[i].toLowerCase() + ".",
                    (i % 4 == 0) ? "Inglés" : "Español",
                    2900 + (i * 450),
                    status,
                    courseTags,
                    modules,
                    new ArrayList<>(),
                    learningPoints,
                    prerequisites,
                    3.0 + (i % 6),
                    4 + (i % 10));

            course.setSubscribersNumber(0);
            courses.add(courseRepository.save(course));
        }

        return courses;
    }

    private List<Module> buildModulesForCourse(int courseIndex) {
        List<Module> modules = new ArrayList<>();
        int moduleCount = 2 + (courseIndex % 2);

        for (int m = 1; m <= moduleCount; m++) {
            Module module = new Module();
            module.setTitle("Módulo " + m + " - Bloque principal");
            module.setDescription("Contenido estructurado del módulo " + m + " para el curso " + courseIndex + ".");
            module.setOrderIndex(m);

            for (int l = 1; l <= 3; l++) {
                Lesson lesson = new Lesson();
                lesson.setTitle("Lección " + m + "." + l + " - Caso práctico");
                lesson.setDescription("Demostración y ejercicios aplicados para reforzar conceptos clave.");
                lesson.setVideoUrl("https://www.youtube.com/embed/demo_" + courseIndex + "_" + m + "_" + l);
                lesson.setOrderIndex(l);
                module.addLesson(lesson);
            }

            modules.add(module);
        }

        return modules;
    }

    private List<Event> initializeEvents(SeedContext context) {
        List<User> creators = List.of(
                context.users.get("admin"),
                context.users.get("content_lead"),
                context.users.get("mentor_ai"));

        String[] titles = {
                "Summit de Innovación 2026", "Webinar Growth Hacking", "Foro de Liderazgo Ejecutivo",
                "Bootcamp de Ventas B2B", "Networking de Founders", "Masterclass de Finanzas Inteligentes",
                "Taller de Comunicación de Impacto", "Jornada Productividad Pro", "Conferencia IA para Negocios",
                "Meetup de Inversores", "Demo Day Startups", "Evento Híbrido de Estrategia Comercial"
        };

        String[] categories = {
                "Conferencia", "Webinar", "Foro", "Bootcamp", "Networking", "Masterclass",
                "Taller", "Jornada", "Conferencia", "Meetup", "Demo Day", "Evento Híbrido"
        };

        String[][] tagMatrix = {
                { "Emprendimiento", "Liderazgo", "Networking" },
                { "Marketing Digital", "Productividad", "Ventas" },
                { "Liderazgo", "Comunicación", "Negociación" },
                { "Ventas", "Negociación", "Productividad" },
                { "Networking", "Startups", "Emprendimiento" },
                { "Finanzas", "Inversión", "Libertad Financiera" },
                { "Comunicación", "Desarrollo Personal", "Liderazgo" },
                { "Gestión del Tiempo", "Productividad", "Desarrollo Personal" },
                { "IA", "Emprendimiento", "Programación" },
                { "Inversión", "Networking", "Finanzas" },
                { "Startups", "Emprendimiento", "Ventas" },
                { "Ventas", "Marketing Digital", "Liderazgo" }
        };

        List<Event> events = new ArrayList<>();
        LocalDateTime baseDate = LocalDateTime.of(2026, 3, 1, 9, 0);

        for (int i = 0; i < titles.length; i++) {
            Event event = new Event();
            event.setCreator(creators.get(i % creators.size()));
            event.setTitle(titles[i]);
            event.setDescription("Evento orientado a casos reales y networking profesional sobre " + titles[i] + ".");
            event.setCategory(categories[i]);
            event.setPriceCents((i % 3 == 0) ? 0 : (1500 + (i * 500)));
            event.setStartDate(baseDate.plusDays(i * 9L));
            event.setEndDate(baseDate.plusDays(i * 9L).plusHours(3 + (i % 5)));
            event.setCapacity(40 + (i * 10));
            event.setAttendeesCount(0);
            event.setSpeakers(List.of("Speaker " + (i + 1), "Guest " + (i + 11)));

            if (i % 4 == 0) {
                event.setStatus(Status.PENDING_REVIEW);
            } else if (i % 7 == 0) {
                event.setStatus(Status.DRAFT);
            } else {
                event.setStatus(Status.PUBLISHED);
            }

            if (i % 3 != 1) {
                event.setLocation(context.locations.get(i % context.locations.size()));
            }

            event.getTags().addAll(toTagSet(context.tags, tagMatrix[i]));

            List<EventSession> sessions = new ArrayList<>();
            sessions.add(new EventSession("09:30", "Apertura", "Contexto de mercado y objetivos del evento"));
            sessions.add(new EventSession("11:00", "Caso práctico", "Aplicación paso a paso con métricas"));
            sessions.add(new EventSession("12:30", "Panel", "Preguntas y networking guiado"));
            event.setSessions(sessions);

            events.add(eventRepository.save(event));
        }

        return events;
    }

    private void initializeEnrollmentsAndLessonProgress(SeedContext context, List<Course> courses) {
        List<Course> publishedCourses = courses.stream()
                .filter(course -> course.getStatus() == Status.PUBLISHED)
                .toList();

        List<User> learners = context.users.values().stream()
                .filter(user -> !user.getUsername().equals("admin") && !user.getUsername().equals("content_lead"))
                .toList();

        List<Enrollment> enrollments = new ArrayList<>();
        Set<String> enrollmentKeys = new HashSet<>();

        int[] progressPattern = { 5, 20, 35, 55, 70, 85, 100 };

        for (int u = 0; u < learners.size(); u++) {
            User learner = learners.get(u);
            int enrollmentCount = 3 + (u % 4);

            for (int j = 0; j < enrollmentCount; j++) {
                Course course = publishedCourses.get((u + j) % publishedCourses.size());
                String key = learner.getId() + "-" + course.getId();
                if (enrollmentKeys.contains(key)) {
                    continue;
                }

                Enrollment enrollment = new Enrollment(learner, course);
                enrollment.setProgressPercentage(progressPattern[(u + j) % progressPattern.length]);
                enrollments.add(enrollment);
                enrollmentKeys.add(key);
            }
        }

        enrollmentRepository.saveAll(enrollments);

        List<LessonProgress> progresses = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            List<Lesson> lessons = course.getModules().stream()
                    .flatMap(module -> module.getLessons().stream())
                    .sorted((a, b) -> a.getOrderIndex().compareTo(b.getOrderIndex()))
                    .toList();

            if (lessons.isEmpty()) {
                continue;
            }

            int completedLessons = Math.round(lessons.size() * (enrollment.getProgressPercentage() / 100f));
            for (int i = 0; i < lessons.size(); i++) {
                LessonProgress progress = new LessonProgress();
                progress.setUser(enrollment.getUser());
                progress.setLesson(lessons.get(i));
                boolean isCompleted = i < completedLessons;
                progress.setIsCompleted(isCompleted);
                if (isCompleted) {
                    progress.setCompletedAt(LocalDateTime.now().minusDays(lessons.size() - i));
                }
                progresses.add(progress);
            }
        }

        lessonProgressRepository.saveAll(progresses);
    }

    private void initializeReviews(SeedContext context, List<Course> courses) {
        List<Course> publishedCourses = courses.stream()
                .filter(course -> course.getStatus() == Status.PUBLISHED)
                .toList();

        List<User> reviewers = context.users.values().stream()
                .filter(user -> user.getUsername().startsWith("learner"))
                .toList();

        List<Review> reviews = new ArrayList<>();
        Set<String> reviewKeys = new HashSet<>();

        for (int c = 0; c < publishedCourses.size(); c++) {
            Course course = publishedCourses.get(c);

            for (int r = 0; r < 6; r++) {
                User reviewer = reviewers.get((c + r) % reviewers.size());
                if (course.getCreator() != null && course.getCreator().getId().equals(reviewer.getId())) {
                    continue;
                }

                String key = reviewer.getId() + "-" + course.getId();
                if (reviewKeys.contains(key)) {
                    continue;
                }

                Review review = new Review();
                review.setCourse(course);
                review.setUser(reviewer);
                review.setRating(3 + ((c + r) % 3));
                review.setContent("Valoración de prueba para " + course.getTitle() + " con feedback detallado #" + (r + 1));
                reviews.add(review);
                reviewKeys.add(key);
            }
        }

        reviewRepository.saveAll(reviews);
    }

    private void initializeEventRegistrations(SeedContext context, List<Event> events) {
        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getStatus() == Status.PUBLISHED)
                .toList();

        List<User> attendees = context.users.values().stream()
                .filter(user -> user.getUsername().startsWith("learner") || user.getUsername().startsWith("mentor")
                        || user.getUsername().startsWith("coach") || user.getUsername().startsWith("finance"))
                .toList();

        List<EventRegistration> registrations = new ArrayList<>();
        Set<String> registrationKeys = new HashSet<>();

        for (int e = 0; e < publishedEvents.size(); e++) {
            Event event = publishedEvents.get(e);
            int target = Math.min(event.getCapacity(), 8 + (e * 2));

            for (int i = 0; i < target; i++) {
                User attendee = attendees.get((e + i) % attendees.size());
                String key = attendee.getId() + "-" + event.getId();
                if (registrationKeys.contains(key)) {
                    continue;
                }

                EventRegistration registration = new EventRegistration();
                registration.setUser(attendee);
                registration.setEvent(event);
                registrations.add(registration);
                registrationKeys.add(key);
            }
        }

        eventRegistrationRepository.saveAll(registrations);
    }

    private void initializeSubscriptions(SeedContext context) {
        List<User> users = context.users.values().stream()
                .filter(user -> user.getUsername().startsWith("learner"))
                .sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()))
                .toList();

        List<Subscription> subscriptions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);

            Subscription subscription = new Subscription();
            subscription.setUser(user);

            if (i < 10) {
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setStartDate(now.minusDays(10 + i));
                subscription.setEndDate(now.plusDays(20 + i));
            } else if (i < 14) {
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                subscription.setStartDate(now.minusDays(80 + i));
                subscription.setEndDate(now.minusDays(10 + i));
            } else {
                subscription.setStatus(SubscriptionStatus.CANCELLED);
                subscription.setStartDate(now.minusDays(30 + i));
                subscription.setEndDate(now.plusDays(5));
            }

            subscriptions.add(subscription);
        }

        subscriptionRepository.saveAll(subscriptions);
    }

    private void initializeOrders(SeedContext context, List<Course> courses, List<Event> events) {
        List<Course> publishedCourses = courses.stream()
                .filter(course -> course.getStatus() == Status.PUBLISHED)
                .toList();

        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getStatus() == Status.PUBLISHED)
                .toList();

        List<User> buyers = context.users.values().stream()
                .filter(user -> user.getUsername().startsWith("learner"))
                .sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()))
                .toList();

        for (int i = 0; i < buyers.size(); i++) {
            User buyer = buyers.get(i);

            Course firstCourse = publishedCourses.get(i % publishedCourses.size());
            Event firstEvent = publishedEvents.get(i % publishedEvents.size());

            createOrderWithItems(
                    buyer,
                    OrderStatus.PAID,
                    "CARD",
                    "PAY-OK-" + String.format("%04d", i + 1),
                    nowMinusDays(i + 1),
                    List.of(
                            orderItemData(firstCourse, null, firstCourse.getPriceCents(), false),
                            orderItemData(null, firstEvent, firstEvent.getPriceCents(), false),
                            orderItemData(null, null, 1999, true)));

            Course secondCourse = publishedCourses.get((i + 4) % publishedCourses.size());
            OrderStatus secondStatus = (i % 5 == 0) ? OrderStatus.FAILED : OrderStatus.PENDING;

            createOrderWithItems(
                    buyer,
                    secondStatus,
                    (secondStatus == OrderStatus.PENDING) ? "PENDING" : "CARD",
                    (secondStatus == OrderStatus.PENDING) ? "PAY-PENDING-" + String.format("%04d", i + 1)
                        : "PAY-ERR-" + String.format("%04d", i + 1),
                    (secondStatus == OrderStatus.PENDING) ? null : nowMinusDays(i + 2),
                    List.of(orderItemData(secondCourse, null, secondCourse.getPriceCents(), false)));

            if (i % 6 == 0) {
                Course refundCourse = publishedCourses.get((i + 2) % publishedCourses.size());
                createOrderWithItems(
                        buyer,
                        OrderStatus.REFUNDED,
                        "CARD",
                        "PAY-RF-" + String.format("%04d", i + 1),
                        nowMinusDays(i + 3),
                        List.of(orderItemData(refundCourse, null, refundCourse.getPriceCents(), false)));
            }
        }
    }

    private LocalDateTime nowMinusDays(int days) {
        return LocalDateTime.now().minusDays(days);
    }

    private OrderItemData orderItemData(Course course, Event event, Integer priceCents, boolean isSubscription) {
        OrderItemData data = new OrderItemData();
        data.course = course;
        data.event = event;
        data.priceCents = priceCents == null ? 0 : priceCents;
        data.isSubscription = isSubscription;
        return data;
    }

    private void createOrderWithItems(User user, OrderStatus status, String paymentMethod, String paymentReference,
            LocalDateTime paidAt, List<OrderItemData> itemDataList) {

        int totalAmount = itemDataList.stream().mapToInt(item -> item.priceCents).sum();

        Order order = new Order();
        order.setUser(user);
        order.setStatus(status);
        order.setTotalAmountCents(totalAmount);
        order.setPaidAt(paidAt);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentReference(paymentReference);
        order.setBillingFullName(user.getUsername() + " Test");
        order.setBillingEmail(user.getEmail());
        order.setCardLast4((status == OrderStatus.PAID || status == OrderStatus.REFUNDED) ? "42" + String.format("%02d", user.getId() % 100) : null);
        order.setInvoicePending(status == OrderStatus.PAID && user.getId() % 3 == 0);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemData data : itemDataList) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setCourse(data.course);
            item.setEvent(data.event);
            item.setPriceAtPurchaseCents(data.priceCents);
            item.setSubscription(data.isSubscription);
            items.add(item);
        }

        savedOrder.setItems(items);
        orderItemRepository.saveAll(items);
        orderRepository.save(savedOrder);
    }

    private void refreshCourseSubscribers(List<Course> courses) {
        Map<Long, Long> subscribersByCourse = enrollmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(enrollment -> enrollment.getCourse().getId(), Collectors.counting()));

        for (Course course : courses) {
            int subscribers = subscribersByCourse.getOrDefault(course.getId(), 0L).intValue();
            course.setSubscribersNumber(subscribers);
        }

        courseRepository.saveAll(courses);
    }

    private void refreshEventAttendees(List<Event> events) {
        Map<Long, Long> attendeesByEvent = eventRegistrationRepository.findAll().stream()
                .collect(Collectors.groupingBy(registration -> registration.getEvent().getId(), Collectors.counting()));

        for (Event event : events) {
            int attendees = attendeesByEvent.getOrDefault(event.getId(), 0L).intValue();
            event.setAttendeesCount(attendees);
        }

        eventRepository.saveAll(events);
    }

    private Set<Tag> toTagSet(Map<String, Tag> tags, String[] tagNames) {
        Set<Tag> result = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tags.get(tagName);
            if (tag != null) {
                result.add(tag);
            }
        }
        return result;
    }

    private static class SeedContext {
        private Map<String, User> users;
        private Map<String, Tag> tags;
        private List<Location> locations;
    }

    private static class OrderItemData {
        private Course course;
        private Event event;
        private int priceCents;
        private boolean isSubscription;
    }
}
