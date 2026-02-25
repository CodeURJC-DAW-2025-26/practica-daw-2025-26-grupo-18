package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.*;
import es.codeurjc.scam_g18.model.Module;
import es.codeurjc.scam_g18.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    @PostConstruct
    public void init() throws IOException, SQLException {
        if (roleRepository.count() == 0) {
            initializeRoles();
            initializeUsers();
            initializeTags();
            initializeCourses();
            initializeEvents();
        }
        ensureDefaultImagesForExistingData();
    }

    private void ensureDefaultImagesForExistingData() throws IOException, SQLException {
        List<Course> courses = courseRepository.findAll();
        String[] courseImagePaths = { "/img/features/features-1.webp", "/img/features/features-2.webp",
                "/img/features/features-3.webp" };

        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            if (course.getImage() == null) {
                String imagePath = courseImagePaths[i % courseImagePaths.length];
                course.setImage(imageService.saveImage(imagePath));
                courseRepository.save(course);
            }
        }

        List<Event> events = eventRepository.findAll();
        String[] eventImagePaths = { "/img/services/services-1.webp", "/img/services/Services-3.webp",
                "/img/services/services-7.webp" };

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event.getImage() == null) {
                String imagePath = eventImagePaths[i % eventImagePaths.length];
                event.setImage(imageService.saveImage(imagePath));
                eventRepository.save(event);
            }
        }
    }

    private void initializeRoles() {
        roleRepository.save(new Role("USER"));
        roleRepository.save(new Role("ADMIN"));
        roleRepository.save(new Role("SUBSCRIBED"));
    }

    private void initializeUsers() {
        // Admin
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@scam.com");
        admin.setPassword(passwordEncoder.encode("adminpass"));
        admin.setGender("MALE");
        admin.setBirthDate(LocalDate.of(1990, 1, 1));
        admin.setShortDescription("Administrador del sistema con acceso total.");
        admin.setCountry("Spain");
        admin.setCurrentGoal("Evolucionar economicamente.");
        admin.setIsActive(true);
        // Roles: USER, ADMIN
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(roleRepository.findByName("USER").orElseThrow());
        adminRoles.add(roleRepository.findByName("ADMIN").orElseThrow());
        admin.setRoles(adminRoles);
        userRepository.save(admin);

        // User
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@scam.com");
        user.setPassword(passwordEncoder.encode("userpass"));
        user.setShortDescription("Usuario estándar del sistema.");
        user.setIsActive(true);
        user.setCurrentGoal("Evolucionar economicamente.");
        user.setCountry("Spain");
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(roleRepository.findByName("USER").orElseThrow());
        user.setRoles(userRoles);
        userRepository.save(user);

        // Creator (Profesor)
        User creator = new User();
        creator.setUsername("juan_inversor");
        creator.setEmail("juan@scam.com");
        creator.setPassword(passwordEncoder.encode("juanpass"));
        creator.setShortDescription("Creador de contenido del sistema.");
        creator.setIsActive(true);
        creator.setCurrentGoal("Evolucionar economicamente.");
        creator.setCountry("Estados Unidos");
        Set<Role> creatorRoles = new HashSet<>();
        creatorRoles.add(roleRepository.findByName("USER").orElseThrow());
        creator.setRoles(creatorRoles);
        userRepository.save(creator);
    }

    private void initializeTags() {
        tagRepository.save(new Tag("Desarrollo Personal"));
        tagRepository.save(new Tag("Emprendimiento"));
        tagRepository.save(new Tag("Finanzas"));
        tagRepository.save(new Tag("Liderazgo"));
        tagRepository.save(new Tag("Productividad"));
        tagRepository.save(new Tag("Libertad Financiera"));
    }

    private void initializeCourses() throws IOException, SQLException {
        User creator = userRepository.findByUsername("admin").orElseThrow();
        User juan = userRepository.findByUsername("juan_inversor").orElseThrow();

        Tag finanzas = tagRepository.findByName("Finanzas").orElseThrow();
        Tag libertad = tagRepository.findByName("Libertad Financiera").orElseThrow();
        Tag emprendimiento = tagRepository.findByName("Emprendimiento").orElseThrow();
        Tag productividad = tagRepository.findByName("Productividad").orElseThrow();
        Tag liderazgo = tagRepository.findByName("Liderazgo").orElseThrow();

        // --- COURSE 1: Libertad Financiera ---
        Set<Tag> tags1 = new HashSet<>();
        tags1.add(finanzas);
        tags1.add(libertad);

        List<String> points1 = new ArrayList<>();
        points1.add("Gestión de activos y pasivos.");
        points1.add("Estrategias de inversión a largo plazo.");
        points1.add("Cómo salir de deudas rápidamente.");

        List<String> prereqs1 = new ArrayList<>();
        prereqs1.add("Ninguno, empezamos desde cero.");

        // Módulo 1 y sus Lecciones
        Module m1c1 = new Module();
        m1c1.setTitle("Módulo 1: Fundamentos del Dinero");
        m1c1.setDescription("Aprende la diferencia real entre activos y pasivos.");
        m1c1.setOrderIndex(1);

        Lesson l1m1c1 = new Lesson();
        l1m1c1.setTitle("Introducción a la Inteligencia Financiera");
        l1m1c1.setDescription("Conceptos básicos que no te enseñan en la escuela.");
        l1m1c1.setVideoUrl("https://www.youtube.com/embed/ejemplo1");
        l1m1c1.setOrderIndex(1);
        m1c1.addLesson(l1m1c1); // addLesson ya hace l1m1c1.setModule(m1c1)

        Lesson l2m1c1 = new Lesson();
        l2m1c1.setTitle("Activos vs Pasivos");
        l2m1c1.setDescription("Por qué tu casa no es una inversión.");
        l2m1c1.setVideoUrl("https://www.youtube.com/embed/ejemplo2");
        l2m1c1.setOrderIndex(2);
        m1c1.addLesson(l2m1c1);

        // Módulo 2 y sus Lecciones
        Module m2c1 = new Module();
        m2c1.setTitle("Módulo 2: Eliminación de Deudas");
        m2c1.setDescription("Estrategia bola de nieve y avalancha.");
        m2c1.setOrderIndex(2);

        Lesson l1m2c1 = new Lesson();
        l1m2c1.setTitle("El método de la bola de nieve");
        l1m2c1.setDescription("Empieza por las deudas más pequeñas para ganar momento.");
        l1m2c1.setVideoUrl("https://www.youtube.com/embed/ejemplo3");
        l1m2c1.setOrderIndex(1);
        m2c1.addLesson(l1m2c1);

        List<Module> modules1 = new ArrayList<>(List.of(m1c1, m2c1));

        Course course1 = new Course(
                creator,
                "Libertad Financiera en 30 días",
                "Consigue tu libertad financiera con nuestro método probado.",
                "Descubre los secretos que los bancos no quieren que sepas...",
                "Español",
                4999, // cents
                Status.PUBLISHED,
                tags1,
                modules1,
                new ArrayList<>(),
                points1,
                prereqs1,
                2.5, // videoHours
                5 // downloadableResources
        );
        course1.setImage(imageService.saveImage("/img/features/features-1.webp"));
        courseRepository.save(course1);

        // --- COURSE 2: Emprende tu Negocio Online ---
        Set<Tag> tags2 = new HashSet<>();
        tags2.add(emprendimiento);
        tags2.add(productividad);

        List<String> points2 = new ArrayList<>();
        points2.add("Validación de ideas de negocio.");
        points2.add("Creación de MVP sin código.");
        points2.add("Marketing de guerrilla.");

        // Módulo 1 y sus Lecciones
        Module m1c2 = new Module();
        m1c2.setTitle("Módulo 1: Encontrar tu Nicho");
        m1c2.setDescription("Técnicas de validación de mercado.");
        m1c2.setOrderIndex(1);

        Lesson l1m1c2 = new Lesson();
        l1m1c2.setTitle("Análisis de Competencia");
        l1m1c2.setDescription("Cómo espiar a tu competencia legalmente.");
        l1m1c2.setVideoUrl("https://www.youtube.com/embed/ejemplo4");
        l1m1c2.setOrderIndex(1);
        m1c2.addLesson(l1m1c2);

        // Módulo 2 y sus Lecciones
        Module m2c2 = new Module();
        m2c2.setTitle("Módulo 2: Tu primer MVP");
        m2c2.setDescription("Herramientas No-Code para lanzar en tiempo récord.");
        m2c2.setOrderIndex(2);

        Lesson l1m2c2 = new Lesson();
        l1m2c2.setTitle("Creando una Landing Page en 1 hora");
        l1m2c2.setDescription("Uso de herramientas gratuitas para captar leads.");
        l1m2c2.setVideoUrl("https://www.youtube.com/embed/ejemplo5");
        l1m2c2.setOrderIndex(1);
        m2c2.addLesson(l1m2c2);

        List<Module> modules2 = new ArrayList<>(List.of(m1c2, m2c2));

        Course course2 = new Course(
                juan,
                "Emprende tu Negocio Online",
                "Lanza tu startup en menos de una semana.",
                "Guía paso a paso para crear un negocio digital rentable desde cero. Sin necesidad de experiencia previa.",
                "Español",
                9999,
                Status.PUBLISHED,
                tags2,
                modules2,
                new ArrayList<>(),
                points2,
                List.of("Ordenador", "Conexión a Internet"),
                5.0, // videoHours (5 horas completas)
                12 // downloadableResources (12 plantillas/archivos)
        );
        course2.setImage(imageService.saveImage("/img/features/features-2.webp"));
        courseRepository.save(course2);

        // --- COURSE 3: Liderazgo Exponencial ---
        Set<Tag> tags3 = new HashSet<>();
        tags3.add(liderazgo);
        tags3.add(productividad);

        Module m1c3 = new Module();
        m1c3.setTitle("Módulo 1: El Mindset del Líder");
        m1c3.setDescription("Cómo transicionar de jefe a líder inspirador.");
        m1c3.setOrderIndex(1);

        Lesson l1m1c3 = new Lesson();
        l1m1c3.setTitle("Escucha Activa");
        l1m1c3.setDescription("El 80% del liderazgo es saber escuchar a tu equipo.");
        l1m1c3.setVideoUrl("https://www.youtube.com/embed/ejemplo6");
        l1m1c3.setOrderIndex(1);
        m1c3.addLesson(l1m1c3);

        List<Module> modules3 = new ArrayList<>(List.of(m1c3));

        Course course3 = new Course(
                creator,
                "Liderazgo Exponencial",
                "Aprende a liderar equipos de alto rendimiento.",
                "El liderazgo no es dar órdenes, es inspirar. Conviértete en el líder que todos quieren seguir. Técnicas de coaching y gestión de equipos multiculturales.",
                "Inglés",
                5999,
                Status.PUBLISHED,
                tags3,
                modules3,
                new ArrayList<>(),
                List.of("Comunicación asertiva", "Delegación efectiva"),
                List.of("Experiencia previa gestionando personas recomendada"),
                7.5, // videoHours (7 horas y media)
                3 // downloadableResources (3 PDFs de resumen)
        );
        course3.setImage(imageService.saveImage("/img/features/features-3.webp"));
        courseRepository.save(course3);
    }

    private void initializeEvents() throws IOException, SQLException {
        User creator = userRepository.findByUsername("admin").orElseThrow();
        Tag liderazgo = tagRepository.findByName("Liderazgo").orElseThrow();
        Tag personal = tagRepository.findByName("Desarrollo Personal").orElseThrow();
        Tag emprendimiento = tagRepository.findByName("Emprendimiento").orElseThrow();

        // Event 1
        Location loc1 = new Location();
        loc1.setName("Palacio de Congresos");
        loc1.setCity("Madrid");
        loc1.setAddress("Paseo de la Castellana 123");
        loc1.setCountry("España");
        loc1.setLatitude(40.45798923792695);
        loc1.setLongitude(-3.6905805690206726);
        locationRepository.save(loc1);

        Set<Tag> tags1 = new HashSet<>();
        tags1.add(liderazgo);
        tags1.add(emprendimiento);

        Event event1 = new Event(
                creator,
                loc1,
                imageService.saveImage("/img/services/services-1.webp"),
                "Cumbre de Liderazgo 2026",
                "El evento más importante para CEOs y directivos en Europa.",
                15000, // 150.00
                LocalDateTime.of(2026, 5, 20, 9, 0),
                LocalDateTime.of(2026, 5, 22, 18, 0),
                "Conferencia",
                Status.PUBLISHED,
                500);
        event1.getTags().addAll(tags1);
        eventRepository.save(event1);

        // Event 2 (Online)
        Set<Tag> tags2 = new HashSet<>();
        tags2.add(personal);

        Event event2 = new Event(
                creator,
                null, // Online has no location
                imageService.saveImage("/img/services/Services-3.webp"),
                "Webinar: Despierta tu Potencial",
                "Taller online intensivo para romper tus barreras mentales.",
                0, // Free
                LocalDateTime.of(2026, 3, 15, 19, 0),
                LocalDateTime.of(2026, 3, 15, 21, 0),
                "Webinar",
                Status.PUBLISHED,
                1000);
        event2.getTags().addAll(tags2);
        eventRepository.save(event2);

        // Event 3
        Location loc3 = new Location();
        loc3.setName("Co-working Space The Shed");
        loc3.setCity("Barcelona");
        loc3.setAddress("Carrer de la Marina 15");
        loc3.setCountry("España");
        locationRepository.save(loc3);

        Set<Tag> tags3 = new HashSet<>();
        tags3.add(emprendimiento);

        Event event3 = new Event(
                creator,
                loc3,
                imageService.saveImage("/img/services/services-7.webp"),
                "Networking & Tapas",
                "Conoce a otros emprendedores en un ambiente distendido.",
                1500, // 15.00
                LocalDateTime.of(2026, 4, 10, 18, 30),
                LocalDateTime.of(2026, 4, 10, 21, 30),
                "Networking",
                Status.PUBLISHED,
                50);
        event3.getTags().addAll(tags3);
        event3.setAttendeesCount(50);
        eventRepository.save(event3);
    }
}
