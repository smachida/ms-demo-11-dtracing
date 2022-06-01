var user = {
  user: "mongo",
  pwd: "mongo",
  roles: [
    {
      role: "dbOwner",
      db: "recommendation-db"
    }
  ]
};

db.createUser(user);
db.createCollection('recommendations')
