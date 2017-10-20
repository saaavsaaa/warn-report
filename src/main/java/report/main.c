#include<sys/types.h>
#include<stdio.h>
#include<dirent.h>
#include<unistd.h>

int main()
{
    DIR   *dir;
    struct   dirent   *ptr;
    char path[20];

    printf("class path:");
    scanf("%s",path);

    dir = opendir(path);
    while((ptr   =   readdir(dir))!=NULL)
    {
        printf("  d_off:%ld d_name: %s\n", ptr->d_off, ptr->d_name);
    }
    closedir(dir);
}