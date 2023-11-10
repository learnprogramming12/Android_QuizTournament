# Android_QuizTournament
This android app is an assignment. 

Develop an Android quiz application using OpenTDB (https://opentdb.com/) with following features 

a. There must be two types of users, admin and player.

b. Admin user:

1.Log into the application using an email/username & password.
2.Create a new quiz tournament. Fields include name, category, difficulty, start date & end date.
3.A quiz tournament consists of 10 questions dynamically fetched from the OpenTDB (https://opentdb.com/). Using either Retrofit or Volley. Questions can be multi-choice, true/false or a mixture of both. 
4.View all quiz tournaments 
5.Update a quiz tournament. Updatable fields include name, start date & end date.
6.Delete a quiz tournament. Prompt the user for deletion.
7.View the number of likes for each quiz tournament. 
c. Player user: 
1.Log into the application using an email/username & password.
2. Display ongoing, upcoming, past & participated quiz tournaments. 
3.Player user should not be able to participate in upcoming, past or participated quiz tournaments.
4.Participate in ongoing quiz tournaments. All player users that enter the same quiz tournament will be presented with the same 10 questions.
5.Questions must presented on separate pages.
6.Display appropriate feedback for correct & incorrect answers. If a question is answered incorrectly, display the correct answer.
7.When the player user’s quiz tournament is completed, display their score out of 10.
8.Like & unlike quiz tournaments
