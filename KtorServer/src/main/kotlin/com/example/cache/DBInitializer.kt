package com.example.cache

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object DBInitializer {
    fun initialize() {
//        Thread.sleep(3000)
        Database.connect(
            "jdbc:postgresql://ktorserver_db_1:5432/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "mysecretpassword"
        )

        transaction {
            SchemaUtils.drop(Competition_Exercises)
            SchemaUtils.drop(Competitions)
            SchemaUtils.drop(Exercise_Scores)
            SchemaUtils.drop(Users)
            SchemaUtils.create(Users)
            SchemaUtils.create(Exercise_Scores)
            SchemaUtils.create(Competitions)
            SchemaUtils.create(Competition_Exercises)

            "insert into users (name) values ('Max');".exec()
            "insert into users (name) values ('Matteo');".exec()
            "insert into competitions (user_id_one, user_id_two, creation_timestamp) values (1,2,54783567);".exec()
            "insert into exercise_scores (user_id, timestamp, exercise_id, score) values (1,347856473,0,30);".exec()
            "insert into exercise_scores (user_id, timestamp, exercise_id, score) values (1,347856473,0,35);".exec()
            "insert into exercise_scores (user_id, timestamp, exercise_id, score) values (2,347856473,0,35);".exec()
            "insert into competition_exercises (competition_id, score_id) values (1,1);".exec();
            "insert into exercise_scores (user_id, timestamp, exercise_id, score) values (2,53454732875,0,1);".exec()
            "insert into competition_exercises (competition_id, score_id) values (1,2);".exec();
            "insert into exercise_scores (user_id, timestamp, exercise_id, score) values (1,43589743892,1,30);".exec()
            "insert into competition_exercises (competition_id, score_id) values (1,3);".exec();
            "insert into exercise_scores (user_id, timestamp, exercise_id, score) values (2,8758782,1,0);".exec()
            "insert into competition_exercises (competition_id, score_id) values (1,4);".exec();
        }
    }

    private fun String.exec() {
        TransactionManager.current().exec(this)
    }
}