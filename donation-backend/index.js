const app = require("express")();
const express = require("express");
const mysql = require("mysql");
const crypto = require("crypto");
const PORT = 8080;
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
const pool = mysql.createPool({
  connectionLimit: 10,
  host: "127.0.0.1",
  user: "root",
  password: "",
  database: "donations",
});
app.listen(PORT, () => console.log("listen on 8080"));

app.get("/donations", (req, res) => {
  pool.getConnection((err, connection) => {
    if (err) throw err;

    connection.query("SELECT * from donate", (err, rows) => {
      connection.release();
      if (!err) {
        res.send(rows);
      } else {
        console.log(err);
      }
    });
  });
});

app.get("/donations/:_id", (req, res) => {
  const { _id } = req.params;
  pool.getConnection((err, connection) => {
    if (err) throw err;

    connection.query(
      "SELECT * from donate WHERE donate._id = '" + _id + "'",
      (err, rows) => {
        connection.release();
        if (!err) {
          res.send(rows);
        } else {
          console.log(err);
        }
      }
    );
  });
});

app.post("/donations/", (req, res) => {
  const upvote = req.body.upvotes;
  const amount = req.body.amount;
  const paymenttype = req.body.paymenttype;
  const _id = crypto.randomBytes(16).toString("hex");
  console.log(upvote);
  console.log(amount);
  console.log(paymenttype);
  console.log(_id);
  pool.getConnection((err, connection) => {
    if (err) throw err;
    connection.query(
      "INSERT INTO donate(_id,upvotes, paymenttype,amount) VALUES ('" +
        _id +
        "'," +
        upvote +
        ", '" +
        paymenttype +
        "'," +
        amount +
        ")",
      (err, rows) => {
        connection.release();
        if (!err) {
          res.send({
            message: "Insert completed",
          });
        } else {
          throw err;
        }
      }
    );
  });
  // console.log(donation_data);
});

app.delete("/donations/:_id", (req, res) => {
  const { _id } = req.params;

  query = "DELETE FROM donate WHERE donate._id = '" + _id + "'";

  pool.getConnection((err, connection) => {
    if (err) throw err;
    connection.query(query, (err, rows) => {
      connection.release();
      if (!err) {
        res.send({
          message: "Delete completed",
        });
      } else throw err;
    });
  });
});

app.delete("/donations", (req, res) => {
  pool.getConnection((err, connection) => {
    if (err) throw err;
    connection.query("DELETE FROM donate WHERE 1", (err, rows) => {
      connection.release();
      if (!err) {
        res.send({
          message: "Reset completed",
        });
      } else throw err;
    });
  });
});
