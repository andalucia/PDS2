if [ $# -eq 1 ]
then
git checkout master
git pull
git merge $1
git push
else
echo "What is the name of the working branch?"
exit 1
fi
