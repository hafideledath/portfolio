import os

from cs50 import SQL
from flask import Flask, redirect, render_template, request, session
from flask_session import Session
from werkzeug.security import check_password_hash, generate_password_hash

from helpers import apology, login_required, lookup, usd

# Please note you will need a valid API for the application to work
# Replace "YOUR_API_KEY" with your API key, otherwise the app will be unable to quote, buy or sell stocks
# The API keys can be generated for free at iexcloud.io
os.environ["API_KEY"] = "YOUR_API_KEY"

# Declare a global variable to keep track of alerts
alert_text = ""

# Configure application
app = Flask(__name__)

# Ensure templates are auto-reloaded
app.config["TEMPLATES_AUTO_RELOAD"] = True

# Custom filter
app.jinja_env.filters["usd"] = usd

# Configure session to use filesystem (instead of signed cookies)
app.config["SESSION_PERMANENT"] = False
app.config["SESSION_TYPE"] = "filesystem"
Session(app)

# Configure CS50 Library to use SQLite database
db = SQL("sqlite:///finance.db")

# Make sure API key is set
if not os.environ.get("API_KEY"):
    raise RuntimeError("API_KEY not set")


@app.after_request
def after_request(response):
    """Ensure responses aren't cached"""
    response.headers["Cache-Control"] = "no-cache, no-store, must-revalidate"
    response.headers["Expires"] = 0
    response.headers["Pragma"] = "no-cache"
    return response


@app.route("/")
@login_required
def index():
    """Show portfolio of stocks"""

    global alert_text

    # Select the user's portfolio
    portfolio = db.execute("SELECT * FROM portfolio WHERE user_id = ?", session["user_id"])
    rows = []
    sum = 0
    # Create a dictionary for every row and append it to a list of rows
    for i in range(len(portfolio) - 1, -1, -1):
        stock = lookup(portfolio[i]["symbol"])
        rows.append({"symbol": stock["symbol"], "name": stock["name"], "shares": portfolio[i]["shares"],
                    "price": usd(stock["price"]), "total": usd(portfolio[i]["shares"] * stock["price"])})
        sum += portfolio[i]["shares"] * stock["price"]
    cash = db.execute("SELECT cash FROM users WHERE id = ?", session["user_id"])[0]["cash"]
    total = sum + cash

    tmp = alert_text
    alert_text = ""

    # Render the portfolio
    return render_template("index.html", rows=rows, cash=usd(cash), total=usd(total), alert_text=tmp)


@app.route("/buy", methods=["GET", "POST"])
@login_required
def buy():
    """Buy shares of stock"""

    global alert_text

    # User reached the form via POST:
    if request.method == "POST":
        symbol = request.form.get("symbol").upper()
        stock = lookup(symbol)
        shares = request.form.get("shares")
        if shares.isdigit():
            shares = int(shares)

        # If the number of shares is not an integer, then return an apology
        else:
            return apology("invalid shares")

        # If the user did not pick a symbol, return an apology
        if not symbol:
            return apology("must provide symbol")

        # If the user did not enter the amount of shares, return an apology
        elif not shares:
            return apology("must provide shares")

        # If the user entered 0 or a negative number of shares, return an apology
        elif shares < 1:
            return apology("invalid shares")

        # If the user inputted an invalid symbol, return an apology
        elif not stock:
            return apology("invalid symbol")

        total = stock["price"] * shares
        user_cash = db.execute("SELECT cash FROM users WHERE id = ?", session["user_id"])[0]["cash"]

        # If the user could not afford the shares, return an apology
        if total > user_cash:
            return apology("can't afford")

        # If the request is valid, update the users cash and portfolio
        db.execute("UPDATE users SET cash = ? WHERE id = ?", user_cash - total, session["user_id"])
        db.execute("INSERT INTO transactions (user_id, symbol, shares) VALUES (?, ?, ?)", session["user_id"], symbol, shares)
        portfolio = db.execute("SELECT shares FROM portfolio WHERE user_id = ? AND symbol=?", session["user_id"], symbol)

        if portfolio:
            db.execute("UPDATE portfolio SET shares = ? WHERE user_id = ? AND symbol = ?",
                       portfolio[0]["shares"] + shares, session["user_id"], symbol)
        else:
            db.execute("INSERT INTO portfolio (user_id, symbol, shares) VALUES (?, ?, ?)", session["user_id"], symbol, shares)

        alert_text = "Bought!"
        return redirect("/")

    # If the user reached the form via GET, render the form
    else:
        return render_template("buy.html")


@app.route("/history")
@login_required
def history():
    """Show history of transactions"""

    transactions = db.execute("SELECT * FROM transactions WHERE user_id = ? ORDER BY datetime ASC", session["user_id"])
    rows = []
    # Create a dictionary for every row of transactions
    for transaction in transactions:
        stock = lookup(transaction["symbol"])
        rows.append({"symbol": stock["symbol"], "shares": transaction["shares"],
                    "price": usd(stock["price"]), "transacted": transaction["datetime"]})

    # Render the user's history
    return render_template("history.html", rows=rows)


@app.route("/login", methods=["GET", "POST"])
def login():
    """Log user in"""

    # Forget any user_id
    session.clear()

    # User reached route via POST (as by submitting a form via POST)
    if request.method == "POST":

        # Ensure username was submitted
        if not request.form.get("username"):
            return apology("must provide username", 403)

        # Ensure password was submitted
        elif not request.form.get("password"):
            return apology("must provide password", 403)

        # Query database for username
        rows = db.execute("SELECT * FROM users WHERE username = ?", request.form.get("username"))

        # Ensure username exists and password is correct
        if len(rows) != 1 or not check_password_hash(rows[0]["hash"], request.form.get("password")):
            return apology("invalid username and/or password", 403)

        # Remember which user has logged in
        session["user_id"] = rows[0]["id"]

        # Redirect user to home page
        return redirect("/")

    # User reached route via GET (as by clicking a link or via redirect)
    else:
        return render_template("login.html")


@app.route("/logout")
def logout():
    """Log user out"""

    # Forget any user_id
    session.clear()

    # Redirect user to login form
    return redirect("/")


@app.route("/quote", methods=["GET", "POST"])
@login_required
def quote():
    """Get stock quote."""

    # User reached the page via POST
    if request.method == "POST":
        stock = lookup(request.form.get("symbol"))

        # If the user did enter a symbol, return an apology
        if not request.form.get("symbol"):
            return apology("must provide symbol")

        # If the user entered an invalid symbol, return an apology
        if not stock:
            return apology("invalid symbol")

        # Otherwise, render the quote
        else:
            quote = f"A share of {stock['name']} ({stock['symbol']}) costs {usd(stock['price'])}."
            return render_template("quote.html", display="none", quote=quote, title="Quoted")

    # If the user accessed the page via GET, render the form
    else:
        return render_template("quote.html")


@app.route("/register", methods=["GET", "POST"])
def register():
    """Register user"""

    global alert_text

    # Clear the user's session id
    session.clear()

    # If the user reached the page via POST
    if request.method == "POST":

        username = request.form.get("username")
        password = request.form.get("password")
        confirmation = request.form.get("confirmation")

        # If the user did not enter a username, return an apology
        if not username:
            return apology("must provide username")

        # If the user did not enter a password or confirmation, return an apology
        elif not password or not confirmation:
            return apology("must provide password")

        # If the password does not match the confirmation, return an apology
        elif password != confirmation:
            return apology("passwords must match")

        # If the username is not available, return an apology
        elif db.execute("SELECT COUNT(*) FROM users WHERE username=?", username)[0]["COUNT(*)"] > 0:
            return apology("username not available")

        id = db.execute("INSERT INTO users (username, hash) VALUES (?, ?)", username, generate_password_hash(password))

        # Set the session id to the user's id
        session["user_id"] = id

        # Redirect to the portfolio
        alert_text = "Registered!"
        return redirect("/")

    # If the user reached the page via GET, render the form
    else:
        return render_template("register.html")


@app.route("/reset_password", methods=["GET", "POST"])
@login_required
def reset_password():
    """Reset user password"""

    global alert_text

    # User reached the page via POST
    if request.method == "POST":
        password = request.form.get("password")
        new_password = request.form.get("new_password")
        confirmation = request.form.get("confirmation")

        # If the user did not enter the current password, return an apology
        if not password:
            return apology("must provide current password")

        # If the user did not enter the new password or confirmation, return an apology
        elif not new_password or not confirmation:
            return apology("must provide new password")

        # If the new password does not match the confirmation, return an apology
        elif new_password != confirmation:
            return apology("passwords must match")

        # If the current password is incorrect, return an apology
        elif not check_password_hash(db.execute("SELECT hash FROM users WHERE id = ?", session["user_id"])[0]["hash"], password):
            return apology("current password is incorrect")

        # If the request is valid, reset the users password
        db.execute("UPDATE users SET hash=? WHERE id = ?", generate_password_hash(new_password), session["user_id"])

        alert_text = "Password updated!"

        # Redirect to the portfolio
        return redirect("/")

    # If the user reached the page via GET, render the form
    else:
        return render_template("reset_password.html")


@app.route("/sell", methods=["GET", "POST"])
@login_required
def sell():
    """Sell shares of stock"""

    global alert_text

    # User reached the page via POST
    if request.method == "POST":
        symbol = request.form.get("symbol")
        total_shares = db.execute("SELECT shares FROM portfolio WHERE user_id = ? AND symbol = ?",
                                  session["user_id"], symbol)[0]["shares"]
        shares = request.form.get("shares")

        if shares.isdigit():
            shares = int(shares)

        # If the number of shares is not an integer, return an apology
        else:
            return apology("invalid shares")

        # If the user did not enter a symbol, return an apology
        if not symbol:
            return apology("must provide symbol")

        # If the user did not enter a number of shares, return an apology
        elif not shares:
            return apology("must provide shares")

        # If the user inputted too many shares or a negative number of shares, return an apology
        elif shares > total_shares or shares < 0:
            return apology("invalid shares")

        # If the user made a valid request:

        # If the user inputted all their shares, delete a row from their portfolio
        if shares == total_shares:
            db.execute("DELETE FROM portfolio WHERE user_id = ? AND symbol=?", session["user_id"], symbol)

        # Otherwise, update a row from their portfolio
        else:
            db.execute("UPDATE portfolio SET shares = ? WHERE user_id = ? AND symbol=?",
                       total_shares - shares, session["user_id"], symbol)

        # Update the user's cash and transactions
        db.execute("UPDATE users SET cash = cash + ? WHERE id = ?", lookup(symbol)["price"] * shares, session["user_id"])
        db.execute("INSERT INTO transactions (user_id, symbol, shares) VALUES (?, ?, ?)", session["user_id"], symbol, 0 - shares)

        alert_text = "Sold!"

        # Render the portfolio
        return redirect("/")

    # If the user reached the page via GET, render the form
    else:
        symbols = []
        for item in db.execute("SELECT symbol FROM portfolio WHERE user_id = ?", session["user_id"]):
            symbols.append(item["symbol"])

        return render_template("sell.html", symbols=symbols)