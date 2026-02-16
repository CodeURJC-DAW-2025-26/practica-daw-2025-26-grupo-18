public List<Course> getFeaturedCourses() {
    // Obtener los 6 cursos destacados por el rating
    return courseRepository.findTop6ByOrderByRatingDesc(); 
}