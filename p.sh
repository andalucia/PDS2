echo "Did you add the files you changed, using git add -u or git add [new files]? [y/n]"
read choice
if [ $choice = 'y' ]
then
if [ $# -eq 3 ]
then
git commit -m "$1"
git push origin $2
else
echo "First argument should your commit message and the second one should be the name of the current branch"
exit 1
fi
else
echo $choice
echo "Well, do so first!"
fi
