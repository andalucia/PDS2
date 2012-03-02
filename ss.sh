if [ $# -eq 1 ]
then
git pull
git branch $1
git checkout $1
git push origin $1
else
echo "Give a name of your branch"
exit 1
fi
