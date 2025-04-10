#ifndef PROCESS
#define PROCESS
class Process {
    private:
    int id;
    int priority;
    int arrival_time;
    int burst_time;
    public:
    int getID() const;
    int getPriority() const;
    int getArrival_time() const;
    int getBurst_time() const ; 
    void setID(int id);
    void setPriority(int priority);
    void setArrival_time(int arrival_time);
    void setBurst_time(int burst_time);
};
#endif