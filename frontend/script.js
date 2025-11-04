const backendUrl = "https://your-backend.onrender.com/api"; // change this after deploy

let currentUser = null;
const $ = (id) => document.getElementById(id);

function show(el) { el.classList.remove('hidden'); }
function hide(el) { el.classList.add('hidden'); }

function setSession(user){
  currentUser = user;
  localStorage.setItem('psp_user', JSON.stringify(user));
}
function clearSession(){
  currentUser = null;
  localStorage.removeItem('psp_user');
}
function restoreSession(){
  const raw = localStorage.getItem('psp_user');
  if(raw){
    currentUser = JSON.parse(raw);
  }
}

async function api(path, options={}){
  const res = await fetch(`${backendUrl}${path}`, options);
  if(!res.ok){
    const txt = await res.text();
    throw new Error(txt || `HTTP ${res.status}`);
  }
  const ct = res.headers.get('content-type')||'';
  if(ct.includes('application/json')) return res.json();
  return res.text();
}

function setNavbar(){
  if(currentUser){
    show($('navbar'));
    $('welcome-text').textContent = `Logged in as ${currentUser.username} (${currentUser.role})`;
  }else{
    hide($('navbar'));
  }
}

// Auth
$('login-btn').addEventListener('click', async () => {
  $('login-error').textContent = '';
  const username = $('login-username').value.trim();
  const password = $('login-password').value.trim();
  try{
    const user = await api('/auth/login', {
      method:'POST',
      headers:{'Content-Type':'application/json'},
      body: JSON.stringify({username, password})
    });
    setSession(user);
    postLogin();
  }catch(e){
    $('login-error').textContent = 'Invalid username or password';
  }
});

$('logout-btn').addEventListener('click', () => {
  clearSession();
  renderLanding();
});

// Modal helpers
function openModal(title, bodyHTML, onConfirm){
  $('modal-title').textContent = title;
  $('modal-body').innerHTML = bodyHTML;
  $('modal-confirm').onclick = async () => {
    try{ await onConfirm(); closeModal(); } catch(e){ alert(e.message); }
  };
  show($('modal'));
}
function closeModal(){ hide($('modal')); }
$('modal-close').addEventListener('click', closeModal);

// Admin UI
async function loadUsers(){
  const users = await api('/users');
  const tbody = $('users-table').querySelector('tbody');
  tbody.innerHTML = '';
  users.forEach(u => {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td>${u.username}</td><td>${u.role}</td>
      <td>
        <button class="btn" data-edit="${u.username}">Edit</button>
        <button class="btn" data-del="${u.username}">Delete</button>
      </td>`;
    tbody.appendChild(tr);
  });
  tbody.querySelectorAll('[data-del]').forEach(btn => btn.addEventListener('click', async (e)=>{
    const username = e.target.getAttribute('data-del');
    if(confirm(`Delete user ${username}?`)){
      await api(`/users/${encodeURIComponent(username)}`, {method:'DELETE'});
      loadUsers();
    }
  }));
  tbody.querySelectorAll('[data-edit]').forEach(btn => btn.addEventListener('click', (e)=>{
    const username = e.target.getAttribute('data-edit');
    openModal('Edit User', `
      <div class="form-row"><label>New Password</label><input id="edit-pass" type="password"></div>
      <div class="form-row"><label>Role</label>
        <select id="edit-role"><option>Admin</option><option>Teacher</option><option>Student</option></select>
      </div>
    `, async ()=>{
      const password = document.getElementById('edit-pass').value;
      const role = document.getElementById('edit-role').value;
      await api(`/users/${encodeURIComponent(username)}`, {
        method:'PUT', headers:{'Content-Type':'application/json'},
        body: JSON.stringify({password, role})
      });
      loadUsers();
    });
  }));
}

$('add-user-btn').addEventListener('click', ()=>{
  openModal('Add User', `
    <div class="form-row"><label>Username</label><input id="new-username"></div>
    <div class="form-row"><label>Password</label><input id="new-password" type="password"></div>
    <div class="form-row"><label>Role</label>
      <select id="new-role"><option>Admin</option><option>Teacher</option><option>Student</option></select>
    </div>
  `, async ()=>{
    const username = document.getElementById('new-username').value.trim();
    const password = document.getElementById('new-password').value.trim();
    const role = document.getElementById('new-role').value;
    await api('/users', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({username, password, role})});
    loadUsers();
  });
});

// Teacher UI
async function loadTeacherProjects(){
  const rows = await api(`/projects?teacher=${encodeURIComponent(currentUser.username)}`);
  const tbody = $('projects-table').querySelector('tbody');
  tbody.innerHTML = '';
  rows.forEach(p => {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td>${p.title}</td><td>${p.description||''}</td>
      <td><button class="btn" data-delp="${p.id}">Delete</button></td>`;
    tbody.appendChild(tr);
  });
  tbody.querySelectorAll('[data-delp]').forEach(btn => btn.addEventListener('click', async (e)=>{
    const id = e.target.getAttribute('data-delp');
    if(confirm('Delete project?')){ await api(`/projects/${id}`, {method:'DELETE'}); loadTeacherProjects(); loadTeacherSubmissions(); }
  }));
}

$('add-project-btn').addEventListener('click', ()=>{
  openModal('New Project', `
    <div class="form-row"><label>Title</label><input id="p-title"></div>
    <div class="form-row"><label>Description</label><textarea id="p-desc"></textarea></div>
  `, async ()=>{
    const title = document.getElementById('p-title').value.trim();
    const description = document.getElementById('p-desc').value.trim();
    await api('/projects', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({teacher: currentUser.username, title, description})});
    loadTeacherProjects();
  });
});

async function loadTeacherSubmissions(){
  // load all projects then map project titles
  const myProjects = await api(`/projects?teacher=${encodeURIComponent(currentUser.username)}`);
  const projectMap = new Map(myProjects.map(p => [p.id, p.title]));

  const subs = await api('/submissions');
  const mine = subs.filter(s => projectMap.has(s.projectId));
  const tbody = $('teacher-submissions-table').querySelector('tbody');
  tbody.innerHTML = '';
  mine.forEach(s => {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td>${s.student}</td><td>${projectMap.get(s.projectId)||s.projectId}</td>
      <td>${new Date(s.timestamp).toLocaleString()}</td>
      <td><a target="_blank" href="${s.fileUrl}">${s.fileName}</a></td>`;
    tbody.appendChild(tr);
  });
}

// Student UI
async function loadStudentProjects(){
  const rows = await api('/projects');
  const tbody = $('student-projects-table').querySelector('tbody');
  tbody.innerHTML = '';
  rows.forEach(p => {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td>${p.title}</td><td>${p.teacher}</td><td>${p.description||''}</td>
      <td>
        <input type="file" data-file for="p${p.id}">
        <button class="btn" data-upload="${p.id}">Upload</button>
      </td>`;
    tbody.appendChild(tr);
  });
  tbody.querySelectorAll('[data-upload]').forEach(btn => btn.addEventListener('click', async (e)=>{
    const id = e.target.getAttribute('data-upload');
    const row = e.target.closest('tr');
    const fileInput = row.querySelector('[data-file]');
    if(!fileInput.files.length){ alert('Choose a file'); return; }
    const form = new FormData();
    form.append('projectId', id);
    form.append('student', currentUser.username);
    form.append('file', fileInput.files[0]);
    try{
      await api('/submissions', { method:'POST', body: form });
      loadStudentSubmissions();
      alert('Uploaded');
    }catch(e){ alert('Upload failed'); }
  }));
}

async function loadStudentSubmissions(){
  const subs = await api(`/submissions?student=${encodeURIComponent(currentUser.username)}`);
  const allProjects = await api('/projects');
  const projectMap = new Map(allProjects.map(p => [p.id, p.title]));
  const tbody = $('student-submissions-table').querySelector('tbody');
  tbody.innerHTML = '';
  subs.forEach(s => {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td>${projectMap.get(s.projectId)||s.projectId}</td>
      <td>${new Date(s.timestamp).toLocaleString()}</td>
      <td><a target="_blank" href="${s.fileUrl}">${s.fileName}</a></td>
      <td><button class="btn" data-dels="${s.id}">Delete</button></td>`;
    tbody.appendChild(tr);
  });
  tbody.querySelectorAll('[data-dels]').forEach(btn => btn.addEventListener('click', async (e)=>{
    const id = e.target.getAttribute('data-dels');
    if(confirm('Delete submission?')){ await api(`/submissions/${id}`, {method:'DELETE'}); loadStudentSubmissions(); }
  }));
}

function renderLanding(){
  hide($('admin-portal')); hide($('teacher-portal')); hide($('student-portal'));
  show($('login-section'));
  setNavbar();
}

async function postLogin(){
  hide($('login-section'));
  setNavbar();
  if(currentUser.role === 'Admin'){
    show($('admin-portal'));
    await loadUsers();
  } else if(currentUser.role === 'Teacher'){
    show($('teacher-portal'));
    await loadTeacherProjects();
    await loadTeacherSubmissions();
  } else {
    show($('student-portal'));
    await loadStudentProjects();
    await loadStudentSubmissions();
  }
}

// Init
restoreSession();
if(currentUser){ postLogin(); } else { renderLanding(); }


