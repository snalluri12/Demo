
from dotenv import load_dotenv
import subprocess
import os

# Define your Vault session ID
load_dotenv()

session_id = os.getenv("VAULT_SESSION_ID")

# Path to your CSV file
csv_path = "order_import_sample.csv "

# Vault API endpoint
vault_url = "https://vaultsystemintegration-candidate-exercise-srinija.veevavault.com/api/v25.1/vobjects/order__c"

# Construct the curl command
curl_command = [
    "curl",
    "-X", "POST",
    "-H", f"Authorization: {session_id}",
    "-H", "Content-Type: text/csv",
    "-H", "Accept: text/csv",
    "--data-binary", f"@{csv_path}",
    vault_url
]

# Run the curl command
subprocess.run(curl_command)
