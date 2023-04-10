# Coroutinekotlinfunction
Basically, coroutines are computations that can be suspended without blocking a thread

    A process is blocked when there is some external reason that it can not be restarted, e.g., an I/O device is unavailable, or a semaphore file is locked. A process is suspended means that the OS has stopped executing it, but that could just be for time-slicing (multitasking). There is no implication that the process can not be resumed immediately.

    Neither of these words, especially blocked, are being used the same as in non-computer contexts.
    
    BLOCKING: Function A has to be completed before Function B continues. The thread is locked for Function A to complete its execution.
    
    fun testRunFunction() {
    // Start a coroutine
    launch {
        println("In start : ${getThreadName()}")
        Thread.sleep(200)
        println("In ended : ${getThreadName()}")
    }

    run {
        println("Out start: ${getThreadName()}")
        Thread.sleep(300)
        println("Out ended: ${getThreadName()}")
    }
}

Found there’s a way to do so, by putting it in runBlocking and add coroutineContext as parameter to launch

fun testRunFunction() {
    runBlocking {
        // Start a coroutine
        launch(coroutineContext) {
            println("In start : ${getThreadName()}")
            Thread.sleep(200)
            println("In ended : ${getThreadName()}")
        }

        run {
            println("Out start: ${getThreadName()}")
            Thread.sleep(300)
            println("Out ended: ${getThreadName()}")
        }
    }
}

Let’s look at the result

Out start: main
Out ended: main
In start : main
In ended : main

Nice, all are run on main thread!
In the run block, there are no non-blocking functions that allow launch to start its work. There’s now at least some suspension functionality seen.

But, it doesn’t seem very useful if all it does can just be done right after the caller function completed. Let check out further…
Replace sleep with delay

Well, let’s use the special function introduced in Kotlin, i.e. delay() to replace Thread.sleep().

fun testRunFunction() {
    runBlocking {
        // Start a coroutine
        launch(coroutineContext) {
            println("In start : ${getThreadName()}")
            delay(200)
            println("In ended : ${getThreadName()}")
        }

        run {
            println("Out start: ${getThreadName()}")
            delay(300)
            println("Out ended: ${getThreadName()}")
        }
    }
} 
    We are using the delay() function that's like Thread.sleep(), but better: it doesn't block a thread, but only suspends the coroutine itself. The thread is returned to the pool while the coroutine is waiting, and when the waiting is done, the coroutine resumes on a free thread in the pool.

Hopefully, this makes it crystal clear the meaning of the suspend function that is not blocking the Thread.
Launching on Android UI Thread

As we know we could launch on the same Thread and run things in parallel, why not let’s try it on the Android Main UI Thread, updating some UI in Parallel.

I wrote a simple App that update 3 different color status bar with some incremental random number, and race to see who reach the final first

The gist of the code as below

private fun startUpdate() {
    resetRun()

    greenJob = launch(Android) {
        startRunning(progressBarGreen)
    }

    redJob = launch(Android) {
        startRunning(progressBarRed)
    }

    blueJob =launch(Android) {
        startRunning(progressBarBlue)
    }
}

private suspend fun startRunning(
           progressBar: RoundCornerProgressBar) {
    progressBar.progress = 0f
    while (progressBar.progress < 1000 && !raceEnd) {
        delay(10)
        progressBar.progress += (1..10).random()
    }
    if (!raceEnd) {
        raceEnd = true
        Toast.makeText(this, "${progressBar.tooltipText} won!", 
              Toast.LENGTH_SHORT).show()
    }
}

Here you could see there are three jobs got launched. And all calling the same function to update their respective progress bar. The bar got updated in a seemingly parallel fashion. All done in the Main UI Thread without spawning other threads
