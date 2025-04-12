#include "RR.hpp"
#include <iostream>

RoundRobinScheduler::RoundRobinScheduler(std::vector < Process > &process,
					 int q)
:Scheduler(process), quantum(q), current_index(0)
{
}

RoundRobinScheduler::RoundRobinScheduler(int q)
:quantum(q), current_index(0)
{
}

int RoundRobinScheduler::schedule()
{
	int executed_id = 0;
	int count = 0;

	if (process_queue.empty()) {
		current_time++;
		return 0;	// Nothing to execute
	}

	while (count < process_queue.size()) {
		Process & p = process_queue[current_index];

		if (p.getArrival_time() <= current_time &&
		    p.getExecution_time() < p.getBurst_time()) {

			executed_id = p.getID();
			int remaining_time =
			    p.getBurst_time() - p.getExecution_time();
			int time_slice = std::min(quantum, remaining_time);

			for (int t = 0; t < time_slice; ++t) {
				p.setExecution_time(p.getExecution_time() + 1);
				current_time++;

				if (p.getExecution_time() == p.getBurst_time()) {
					p.setwaiting_time(current_time -
							  p.getArrival_time() -
							  p.getBurst_time());
					p.setturnaround_time(current_time -
							     p.
							     getArrival_time());
					break;
				}
			}

			// Move to next index for next round
			current_index =
			    (current_index + 1) % process_queue.size();
			return executed_id;
		}
		// Move to next process if this one isn't ready
		current_index = (current_index + 1) % process_queue.size();
		count++;
	}

	// If no process was ready to run, just increment time
	current_time++;
	return 0;
}
