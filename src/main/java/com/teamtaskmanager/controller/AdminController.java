package com.teamtaskmanager.controller;

import com.teamtaskmanager.dto.ProjectDto;
import com.teamtaskmanager.model.*;
import com.teamtaskmanager.service.ProjectService;
import com.teamtaskmanager.service.TaskService;
import com.teamtaskmanager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    // DASHBOARD

    @GetMapping("/admin_dashboard")
    public String adminDashboard(HttpSession session, Model model){

        try {

            User user = (User) session.getAttribute("loggedInUser");

            if (user == null || user.getRole() != Role.ADMIN) {
                return "redirect:/login";
            }

            model.addAttribute("projects", projectService.getAllProjects());
            model.addAttribute("tasks", taskService.getAllTasks());

            // completed tasks
            model.addAttribute("completedTasks",
                    taskService.getAllTasks().stream()
                            .filter(t -> t.getStatus() == TaskStatus.DONE)
                            .count());

            // pending tasks
            model.addAttribute("pendingTasks",
                    taskService.getAllTasks().stream()
                            .filter(t -> t.getStatus() != TaskStatus.DONE)
                            .count());

            // overdue tasks
            model.addAttribute("overdueTasks",
                    taskService.getAllTasks().stream()
                            .filter(t -> t.getDueDate() != null)
                            .filter(t -> t.getDueDate().isBefore(LocalDate.now()))
                            .filter(t -> t.getStatus() != TaskStatus.DONE)
                            .toList());

            return "admin/admin_dashboard";

        } catch (Exception e) {

            logger.error("Dashboard Error: {}", e.getMessage(), e);

            return "redirect:/login";
        }
    }

    // ADD PROJECT PAGE

    @GetMapping("/add_project")
    public String addProjectPage(){

        try {

            return "admin/add_project";

        } catch (Exception e){

            logger.error("Add Project Page Error: {}", e.getMessage(), e);

            return "redirect:/admin/admin_dashboard";
        }
    }

    // SAVE PROJECT

    @PostMapping("/save-project")
    public String saveProject(@ModelAttribute ProjectDto projectDto){

        try {

            Project project = new Project();

            project.setProjectName(projectDto.getProjectName());
            project.setDescription(projectDto.getDescription());

            projectService.saveProject(project);

        } catch (Exception e){

            logger.error("Save Project Error: {}", e.getMessage(), e);
        }

        return "redirect:/admin/admin_dashboard";
    }

    // DELETE PROJECT

    @GetMapping("/delete-project/{id}")
    public String deleteProject(@PathVariable Long id) {

        try {

            projectService.deleteProject(id);

        } catch (Exception e){

            logger.error("Delete Project Error: {}", e.getMessage(), e);
        }

        return "redirect:/admin/admin_dashboard";
    }

    // ADD TASK PAGE

    @GetMapping("/add_task")
    public String addTask(Model model){

        try {

            model.addAttribute("projects", projectService.getAllProjects());

            model.addAttribute("users", userService.getAllUsers());

            return "admin/add_task";

        } catch (Exception e){

            logger.error("Add Task Page Error: {}", e.getMessage(), e);

            return "redirect:/admin/admin_dashboard";
        }
    }

    // SAVE TASK

    @PostMapping("/save-task")
    public String saveTask(@ModelAttribute Task task) {

        try {

            task.setStatus(TaskStatus.TODO);

            taskService.saveTask(task);

        } catch (Exception e){

            logger.error("Save Task Error: {}", e.getMessage(), e);

        }

        return "redirect:/admin/admin_dashboard";
    }

    // DELETE TASK

    @GetMapping("/delete-task/{id}")
    public String deleteTask(@PathVariable Long id) {

        try {

            taskService.deleteTask(id);

        } catch (Exception e){

            System.out.println("Delete Task Error : " + e.getMessage());
        }

        return "redirect:/admin/admin_dashboard";
    }

    // EDIT TASK PAGE

    @GetMapping("/edit-task/{id}")
    public String editTask(@PathVariable Long id, Model model) {

        try {

            Task task = taskService.getTaskById(id);

            model.addAttribute("task", task);

            model.addAttribute("projects", projectService.getAllProjects());

            model.addAttribute("users", userService.getAllUsers());

            return "admin/edit_task";

        } catch (Exception e){

            logger.error("Edit Task Error: {}", e.getMessage(), e);


            return "redirect:/admin/admin_dashboard";
        }
    }

    // UPDATE TASK

    @PostMapping("/update-task")
    public String updateTask(@ModelAttribute Task task) {

        try {

            taskService.saveTask(task);

        } catch (Exception e){

            logger.error("Update Task Error: {}", e.getMessage(), e);
        }

        return "redirect:/admin/admin_dashboard";
    }

    // TEAM PAGE

    @GetMapping("/team")
    public String team(Model model) {

        try {

            model.addAttribute("users", userService.getAllUsers());

            return "admin/team";

        } catch (Exception e){

            logger.error("Team Page Error: {}", e.getMessage(), e);

            return "redirect:/admin/admin_dashboard";
        }
    }

    // DELETE USER

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {

        try {

            userService.deleteUserById(id);

        } catch (Exception e){

            logger.error("Delete User Error: {}", e.getMessage(), e);
        }

        return "redirect:/admin/team";
    }
}