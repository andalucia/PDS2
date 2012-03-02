if [ $# -eq 1 ]
then
git pull origin $1
else
echo "What is the name of the current branch?"
exit 1
fi
