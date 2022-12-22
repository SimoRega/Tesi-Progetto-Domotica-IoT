#ifndef __IDLE_TASK__
#define __IDLE_TASK__

#include "Task.h"


class IdleTask : public Task{
    public:
        IdleTask(){
            init(200);
        }
        void init(long period){
            Task::init(period);
        }

        void tick();
    
    private:
        bool on=false;
};
#endif