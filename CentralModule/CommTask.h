#ifndef __COMM_TASK__
#define __COMM_TASK__

#include "Task.h"


class CommTask : public Task{
    public:
        CommTask(){
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