import sqlite3
from flask import Flask,render_template,request,jsonify

app = Flask(__name__)

@app.route("/")
def display_site():
    connect_db = sqlite3.connect('/home/chipchip/collectinfo.db')
    connect_db.row_factory = sqlite3.Row
    cursor = connect_db.cursor()
    sqlite_Query =""" SELECT * FROM collectinfo """
    results = cursor.execute(sqlite_Query).fetchall()
    for i in results:
        print(i[4])
    return render_template("display_data.html",results=results)

@app.route('/api',methods=['POST'])
def post_json_data():
    content_type = request.headers.get('Content-Type')
    request_data = request.get_json()
    name_Device = request_data['nameDevice']
    IMEI = request_data['IMEI']
    number_Phone = request_data['numberPhone']
    message_Phone = request_data['messagePhone']
    location_Phone = request_data['locationPhone']
    IP = request_data['IP']
    connect_db = sqlite3.connect('/home/chipchip/collectinfo.db')
    sqlite_Insert = '''INSERT INTO collectinfo (nameDevice,IMEI,numberPhone,
    messagePhone,locationPhone) VALUES(?,?,?,?,?)'''
    connect_db.execute(sqlite_Insert,(name_Device, IMEI, number_Phone, message_Phone,location_Phone,IP))
    connect_db.commit()
    connect_db.close()
    return jsonify({'result' : 'Success!', 'namedevice':name_Device, 'IMEI':IMEI,'NumberPhone':number_Phone,'messagePhone':message_Phone,'location':location_Phone,'IP':IP })

if __name__ == '__main__':
    app.run(debug=True,port=5007)
