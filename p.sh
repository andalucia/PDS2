if [ $# -eq 2 ]
then
git commit -m $1
git push origin $2
else
echo "First argument should your commit message and the second one should be the name of the current branch"
exit 1
fi
