#ifndef __WORKING_TASK__
#define __WORKING_TASK__

#include "Task.h"


class WorkingTask : public Task{
    public:
        WorkingTask(){
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