#!/usr/bin/env python

import urllib2
import hashlib
import hmac
import base64
from datetime import datetime

def sign(secret, key, ip, billingAccountNumber, date):
    to_sign = '%s%s%s%s' % (key, ip, billingAccountNumber, date)
    signature = hmac.new(secret, to_sign, hashlib.sha1).digest()
    return base64.b64encode(signature)

def do_req(key, secret, base, ip, billingAccountNumber):
    now = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S.000+0000')

    url = '%s/%s/%s' % (base.rstrip('/'), ip, billingAccountNumber)
    headers = {}
    headers['Date'] = now
    headers['Accept'] = 'application/json'
    headers['Authorization'] = '%s:%s' % (key, sign(secret, key, ip, billingAccountNumber, now))

    req = urllib2.Request(url, headers=headers)
    try:
        resp = urllib2.urlopen(req)
    except urllib2.HTTPError, e:
        resp = e

    return resp

import sys

if len(sys.argv) < 5:
    sys.stderr.write('usage: %s <key> <secret> <baseUrl> <ip> <billing_account_number>\n' % sys.argv[0])
    sys.exit(1)

key = sys.argv[1]
secret = sys.argv[2]
base = sys.argv[3]
ip = sys.argv[4]
billingAccountNumber = sys.argv[5]

resp = do_req(key, secret, base, ip, billingAccountNumber)
print resp.code
print resp.headers
print resp.read()
