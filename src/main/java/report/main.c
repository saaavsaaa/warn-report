#include<sys/types.h>
#include<stdio.h>
#include<dirent.h>
#include<unistd.h>

int main()
{
    DIR   *dir;
    struct   dirent   *ptr;

    dir = opendir("/home/aaa/Code");

    while((ptr   =   readdir(dir))!=NULL)
    {
        printf("  d_off:%ld d_name: %s\n", ptr->d_off, ptr->d_name);
    }
    closedir(dir);
}
