package com.example.test.data


class sessionsRepo(private val dao: ExerciseDao) {


    // Called when you press the "Save" button in Screen C
    suspend fun saveSessionExercises(
        date: String,              // The date of Screen A
        exerciseName: String,        // The exercise picked from Screen B
        weight: Float, // List of (Weight, Reps) entered by user
        reps: Int
        ) {
        // 1. Get or Create the workoutSessions for this date
        var session = dao.getSessionByDate(date)
        if (session == null) {
            val sessionId = dao.insertSession(workoutSessions(date = date))
            session = workoutSessions(id = sessionId, date = date)
        }

        var exercise = dao.getExerciseByName(exerciseName)

        // 2. Create the WorkoutExercise (the card)
        var sessionExercise = dao.getSessionExercise(session.id, exercise.id)

        if (sessionExercise == null) {
            val cardId = dao.insertSessionExercise(
                sessionExercises(
                    sessionId = session.id,
                    exerciseId = exercise.id,
                    orderIndex = 0
                )
            )
            // Fetch the full object (or just use the ID)
            sessionExercise = sessionExercises(id = cardId, sessionId = session.id, exerciseId = exercise.id, orderIndex = 0)
        }

        val nextSetNumber = dao.getMaxSetNumber(sessionExercise.id) + 1

        // 3. Insert all the sets for this exercise
        val setEntity = sessionSets(
            sessionExerciseId = sessionExercise.id,
            setNumber = nextSetNumber,
            weight = weight,
            reps = reps
        )
        dao.insertSet(setEntity)
    }


    // Called when Screen A loads or the date changes
    suspend fun getWorkoutForDate(date: String): List<ExerciseCardData> {
        // 1. Find the session for this date
        val session = dao.getSessionByDate(date)
        // 2. If no session, return empty list (no cards)
        return if (session == null) emptyList()
        else dao.getExerciseCardsForSession(session.id)
    }
}