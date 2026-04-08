<%@ page contentType="text/html;charset=UTF-8" %>

<h2>👤 Member Profile</h2>

<!-- ================= VIEW SECTION ================= -->
<div id="viewSection">

    <table border="1" cellpadding="10">

        <tr><td>Name</td><td>${name}</td></tr>
        <tr><td>Phone</td><td>${phone}</td></tr>
        <tr><td>Gender</td><td>${gender}</td></tr>
        <tr><td>Age</td><td>${age}</td></tr>
        <tr><td>WhatsApp</td><td>${whatsapp}</td></tr>
        <tr><td>Address</td><td>${address}</td></tr>
        <tr><td>Height</td><td>${height}</td></tr>
        <tr><td>Weight</td><td>${weight}</td></tr>

        <tr><td>Package</td><td>${months} Months</td></tr>
        <tr><td>Start Date</td><td>${startDate}</td></tr>
        <tr><td>End Date</td><td>${endDate}</td></tr>

    </table>

    <br>

    <!-- 🔥 ACTION BUTTONS -->
    <button onclick="showEdit()">✏️ Update</button>

    <form action="view-member" method="post" style="display:inline;">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="fid" value="${fid}">
        <button type="submit" onclick="return confirm('Are you sure to delete?')">❌ Delete</button>
    </form>

</div>

<!-- ================= EDIT SECTION ================= -->
<div id="editSection" style="display:none;">

    <form action="view-member" method="post">

        <input type="hidden" name="action" value="update">
        <input type="hidden" name="fid" value="${fid}">

        <table border="1" cellpadding="10">

            <tr><td>Name</td><td><input type="text" name="name" value="${name}"></td></tr>
            <tr><td>Phone</td><td><input type="text" name="phone" value="${phone}"></td></tr>
            <tr><td>Gender</td><td><input type="text" name="gender" value="${gender}"></td></tr>
            <tr><td>Age</td><td><input type="number" name="age" value="${age}"></td></tr>
            <tr><td>WhatsApp</td><td><input type="text" name="whatsapp" value="${whatsapp}"></td></tr>
            <tr><td>Address</td><td><input type="text" name="address" value="${address}"></td></tr>
            <tr><td>Height</td><td><input type="text" name="height" value="${height}"></td></tr>
            <tr><td>Weight</td><td><input type="text" name="weight" value="${weight}"></td></tr>

            <tr><td>Months</td><td><input type="number" name="months" value="${months}"></td></tr>
            <tr><td>Start Date</td><td><input type="date" name="startDate" value="${startDate}"></td></tr>
            <tr><td>End Date</td><td><input type="date" name="endDate" value="${endDate}"></td></tr>

        </table>

        <br>

        <button type="submit">💾 Save Changes</button>
        <button type="button" onclick="cancelEdit()">Cancel</button>

    </form>

</div>

<br><br>

<a href="fingerprint-data?page=users">⬅ Back to Members</a>

<!-- ================= JS ================= -->
<script>
    function showEdit(){
        document.getElementById("viewSection").style.display="none";
        document.getElementById("editSection").style.display="block";
    }

    function cancelEdit(){
        document.getElementById("viewSection").style.display="block";
        document.getElementById("editSection").style.display="none";
    }
</script>