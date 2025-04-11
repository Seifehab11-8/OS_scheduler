#ifndef PROCESS
#define PROCESS

extern int ID;

class Process
{
private:
    int id;
    int priority;
    int arrival_time;
    int burst_time;
    int execution_time;
    int waiting_time;
    int turnaround_time;

public:
    Process(int arrival_time, int burst_time, int priority = 0);
    int getID() const;
    int getPriority() const;
    int getArrival_time() const;
    int getBurst_time() const;
    int getExecution_time() const;
    int getwaiting_time() const;
    int getturnaround_time() const;
    void setID(int id);
    void setPriority(int priority);
    void setArrival_time(int arrival_time);
    void setBurst_time(int burst_time);
    void setExecution_time(int burst_time);
    void setwaiting_time(int waiting_time);
    void setturnaround_time(int turnaround_time);
};
#endif