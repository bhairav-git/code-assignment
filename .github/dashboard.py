import streamlit as st
import json

# Load JSON report
with open("reports/jscpd-report.json", "r") as file:
    data = json.load(file)

# Dashboard UI
st.title("Duplicate Code Scanner Dashboard")
st.metric("Total Duplicates", len(data.get("duplicates", [])))

for dup in data.get("duplicates", []):
    st.write(f"🔄 Duplicate: {dup['firstFile']['name']} ↔ {dup['secondFile']['name']}")
