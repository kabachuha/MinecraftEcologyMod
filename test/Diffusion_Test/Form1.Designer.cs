namespace Diffusion_Test
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.label0_0 = new System.Windows.Forms.Label();
            this.button1 = new System.Windows.Forms.Button();
            this.panel1 = new System.Windows.Forms.Panel();
            this.reset_button = new System.Windows.Forms.Button();
            this.addition_meter = new System.Windows.Forms.NumericUpDown();
            this.linkLabel1 = new System.Windows.Forms.LinkLabel();
            this.button2 = new System.Windows.Forms.Button();
            this.timer1 = new System.Windows.Forms.Timer(this.components);
            this.timer_toggle = new System.Windows.Forms.Button();
            this.timinter = new System.Windows.Forms.NumericUpDown();
            this.coefmter = new System.Windows.Forms.NumericUpDown();
            this.panel1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.addition_meter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.timinter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.coefmter)).BeginInit();
            this.SuspendLayout();
            // 
            // label0_0
            // 
            this.label0_0.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.label0_0.Font = new System.Drawing.Font("Times New Roman", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label0_0.Location = new System.Drawing.Point(3, 0);
            this.label0_0.Name = "label0_0";
            this.label0_0.Size = new System.Drawing.Size(55, 55);
            this.label0_0.TabIndex = 0;
            this.label0_0.Text = "X";
            this.label0_0.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(163, 9);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(102, 23);
            this.button1.TabIndex = 1;
            this.button1.TabStop = false;
            this.button1.Text = "Force Render Update";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // panel1
            // 
            this.panel1.AutoSize = true;
            this.panel1.Controls.Add(this.label0_0);
            this.panel1.Location = new System.Drawing.Point(12, 41);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(820, 520);
            this.panel1.TabIndex = 2;
            // 
            // reset_button
            // 
            this.reset_button.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.reset_button.Location = new System.Drawing.Point(757, 12);
            this.reset_button.Name = "reset_button";
            this.reset_button.Size = new System.Drawing.Size(75, 23);
            this.reset_button.TabIndex = 1;
            this.reset_button.Text = "Reset";
            this.reset_button.UseVisualStyleBackColor = true;
            this.reset_button.Click += new System.EventHandler(this.reset_button_Click);
            // 
            // addition_meter
            // 
            this.addition_meter.Increment = new decimal(new int[] {
            10,
            0,
            0,
            0});
            this.addition_meter.Location = new System.Drawing.Point(94, 12);
            this.addition_meter.Maximum = new decimal(new int[] {
            9999,
            0,
            0,
            0});
            this.addition_meter.Name = "addition_meter";
            this.addition_meter.Size = new System.Drawing.Size(63, 20);
            this.addition_meter.TabIndex = 4;
            this.addition_meter.Value = new decimal(new int[] {
            10,
            0,
            0,
            0});
            // 
            // linkLabel1
            // 
            this.linkLabel1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.linkLabel1.AutoSize = true;
            this.linkLabel1.Location = new System.Drawing.Point(12, 564);
            this.linkLabel1.Name = "linkLabel1";
            this.linkLabel1.Size = new System.Drawing.Size(253, 13);
            this.linkLabel1.TabIndex = 5;
            this.linkLabel1.TabStop = true;
            this.linkLabel1.Text = "https://github.com/Artem226/MinecraftEcologyMod";
            // 
            // button2
            // 
            this.button2.Location = new System.Drawing.Point(12, 9);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(75, 23);
            this.button2.TabIndex = 0;
            this.button2.Text = "Update";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.update_full);
            // 
            // timer1
            // 
            this.timer1.Interval = 500;
            this.timer1.Tick += new System.EventHandler(this.timer_tick);
            // 
            // timer_toggle
            // 
            this.timer_toggle.Location = new System.Drawing.Point(272, 9);
            this.timer_toggle.Name = "timer_toggle";
            this.timer_toggle.Size = new System.Drawing.Size(75, 23);
            this.timer_toggle.TabIndex = 6;
            this.timer_toggle.Text = "Start Timer";
            this.timer_toggle.UseVisualStyleBackColor = true;
            this.timer_toggle.Click += new System.EventHandler(this.timer_toggle_Click);
            // 
            // timinter
            // 
            this.timinter.Increment = new decimal(new int[] {
            100,
            0,
            0,
            0});
            this.timinter.Location = new System.Drawing.Point(354, 9);
            this.timinter.Maximum = new decimal(new int[] {
            1000000,
            0,
            0,
            0});
            this.timinter.Minimum = new decimal(new int[] {
            10,
            0,
            0,
            0});
            this.timinter.Name = "timinter";
            this.timinter.Size = new System.Drawing.Size(64, 20);
            this.timinter.TabIndex = 7;
            this.timinter.Value = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            // 
            // coefmter
            // 
            this.coefmter.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.coefmter.DecimalPlaces = 8;
            this.coefmter.Increment = new decimal(new int[] {
            1,
            0,
            0,
            196608});
            this.coefmter.Location = new System.Drawing.Point(702, 562);
            this.coefmter.Maximum = new decimal(new int[] {
            99999999,
            0,
            0,
            524288});
            this.coefmter.Name = "coefmter";
            this.coefmter.Size = new System.Drawing.Size(130, 20);
            this.coefmter.TabIndex = 8;
            this.coefmter.Value = new decimal(new int[] {
            1,
            0,
            0,
            196608});
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(844, 580);
            this.Controls.Add(this.coefmter);
            this.Controls.Add(this.timinter);
            this.Controls.Add(this.timer_toggle);
            this.Controls.Add(this.button2);
            this.Controls.Add(this.linkLabel1);
            this.Controls.Add(this.addition_meter);
            this.Controls.Add(this.reset_button);
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.button1);
            this.Name = "Form1";
            this.Text = "EcologyMod - Diffusion Test";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.panel1.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.addition_meter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.timinter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.coefmter)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label0_0;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Button reset_button;
        private System.Windows.Forms.NumericUpDown addition_meter;
        private System.Windows.Forms.LinkLabel linkLabel1;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.Timer timer1;
        private System.Windows.Forms.Button timer_toggle;
        private System.Windows.Forms.NumericUpDown timinter;
        private System.Windows.Forms.NumericUpDown coefmter;
    }
}

