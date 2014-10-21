### STREAM (Simple Task REader And Manager)

This is a project by the awesome group **CS2103AUG2014-F10-1J**, aiming to create a user-friendly to-do-list/task manager, which we call **STREAM**.

Meet the awesome developers of **STREAM**:
* *Wilson Kurniawan*: Team leader, scheduling/tracking/deadlines; in-charge of ui package
* *John Kevin Tjahjadi*: Documentations and code quality controller; in-charge of model package 
* *Steven Khong Wai How*: Eclipse expert, testing master; in-charge of fileio package
* *Jiang Shenhao*: Integration; in-charge of parser package

Currently, STREAM is capable of basic CRUD (Create, Read, Update, Delete) operations and multiple undo operation, accompanied with user-friendly Graphical User Interface (GUI). A quick user-guide:

**add** *taskName*: adds a new task
**delete** *indexNo*: deletes a task
**clear**: clears all tasks
**modify** *indexNo* *newName*: renames a task
**desc** *indexNo* *description*: updates the task's description
**due**  *indexNo* *dueDate*: updates the task's due date
**mark** *indexNo* *done/ongoing*: marks a task as either done or ongoing (not done)
**view** *indexNo*: view the details of a task
**tag** *indexNo* *tags*: adds tags to a task
**untag** *indexNo* *tags*: removes tags from a task
**search** *keyphrase*: searches tasks that match the keyphrase
**clrsrc**: clears search result
**undo**: undoes the last operation
**exit**: exits **STREAM**

We support multiple, integrated command such as
**add** *some task* **due** *28/11* **desc** *buy Pokemon Omega Ruby* **tag** *#awesome #newgame #whatisfinals*

We aim to add more features and improve this project to even greater heights.

*Cheers,*
*STREAM Developers*