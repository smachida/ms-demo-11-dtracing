var user = {
  user: "mongo",
  pwd: "mongo",
  roles: [
    {
      role: "dbOwner",
      db: "product-db"
    }
  ]
};

db.createUser(user);
db.createCollection('products')
